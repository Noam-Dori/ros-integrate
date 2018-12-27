package ros.integrate.workspace;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.containers.MultiMap;
import com.intellij.util.containers.SortedList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.lang.ROSPktLanguage;
import ros.integrate.pkt.psi.ROSPktFile;
import ros.integrate.workspace.psi.ROSPackage;
import ros.integrate.workspace.psi.impl.ROSSourcePackage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * a default finder used for finding packages within the project
 */
public class ROSProjectPackageFinder implements ROSPackageFinder {
    private static final Logger LOG = Logger.getInstance("#ros.integrate.workspace.ROSProjectPackageFinder");

    @Override
    public void findAndCache(Project project, ConcurrentMap<String, ROSPackage> pkgCache) {
        /*
         * the files that actually determine whether or not a directory is a package is the package.xml file.
         * the following condition is true:
         * A directory is a ROS package iff one of its roots directly contains a package.xml file
         */
        List<VirtualFile> packageXmls = FileTypeIndex.getFiles(XmlFileType.INSTANCE, GlobalSearchScope.allScope(project))
                .stream().filter(xml -> xml.getName().equals(ROSPackageUtil.PACKAGE_XML)).collect(Collectors.toList());
        for (VirtualFile vXml : packageXmls) {
            ROSPackage newPkg = investigateXml(vXml, project, pkgCache);
            if (newPkg != ROSPackage.ORPHAN) {
                pkgCache.putIfAbsent(newPkg.getName(), newPkg);
            }
        }
    }

    @NotNull
    private ROSPackage investigateXml(@NotNull VirtualFile vXml, Project project,
                                      @Nullable ConcurrentMap<String, ROSPackage> pkgCache) {
        // 1. get package name
        // FIXME for now, since we don't want to read into files just yet, we will use the directory to name packages.
        String pkgName = vXml.getParent().getName();
        // 2. search for the package in the cache. If it exists, move on to next xml.
        if (pkgCache != null && pkgCache.getOrDefault(pkgName, ROSPackage.ORPHAN) != ROSPackage.ORPHAN)
            return ROSPackage.ORPHAN;
        // 3. get package.xml file in XML PSI form
        XmlFile pkgXml = (XmlFile) Objects.requireNonNull(PsiManager.getInstance(project).findFile(vXml));
        // 4. get package root dir
        PsiDirectory pkgRoot = pkgXml.getContainingDirectory();
        // 5. TODO try getting CMakeLists.txt since this is a project package
        // 6. find all packet files
        // FIXME for now they will just be searched in the project regardless of CMakeLists.txt
        List<ROSPktFile> pkgPackets = findPacketFiles(pkgRoot/*,pkgCMake*/);
        // 7. make package and cache it. MAKE SURE IT IS NOT ROSPackage.ORPHAN
        ROSPackage newPkg = createROSPackage(project, pkgName, pkgRoot, pkgXml, pkgPackets);
        if (newPkg == ROSPackage.ORPHAN) {
            LOG.error("Failed indexing a valid ROS package",
                    "the project package finder tried finding a ros package, and failed.",
                    "Name: [" + pkgName + "]",
                    "Root: [" + pkgRoot.getVirtualFile().getPath() + "]");
        }
        return newPkg;
    }

    @Override
    public MultiMap<ROSPackage, VFileEvent> investigate(@NotNull Project project, @NotNull Collection<VFileEvent> events) {
        MultiMap<ROSPackage, VFileEvent> ret = new MultiMap<>();
        // 1. check who is under the jurisdiction of this finder
        List<VFileEvent> projectEvents = events.stream()
                .filter(event -> belongsToProject(event, project)).collect(Collectors.toList());
        // 2. do XML parents first and get a list of parent directories, use these to create ROSSourcePackages using original method.
        projectEvents.stream().filter(ROSPackageUtil::isPackageXml).forEach(event -> {
            ROSPackage newPkg = investigateXml(Objects.requireNonNull(ROSPackageUtil.getXml(event)),
                    project, null);
            if(newPkg != ROSPackage.ORPHAN) {
                ret.putValue(newPkg, event);
            }
        });
        sortEventsByPkgRoot(projectEvents, ret);
        return ret;
    }

    @Override
    public CacheCommand investigateChanges(Project project, ROSPackage pkg) {
        // 0. check the package is under the jurisdiction of this finder.
        if(!(pkg instanceof ROSSourcePackage)) {
            return null;
        } // this has one sole root.
        if(notInProject(project, pkg.getRoots()[0].getVirtualFile())) { // something is up with the root
            if(pkg.getRoots()[0].getParentDirectory() != null && // check parent - if its in the project, the dir was deleted.
                    notInProject(project, pkg.getRoots()[0].getParentDirectory().getVirtualFile())) {
                return null;
            }
            return CacheCommand.DELETE;
        }
        // 1. check package XML exists. if some don't -> return DELETE.
        XmlFile newXml = ROSPackageUtil.findPackageXml(pkg.getRoots()[0]);
        if(newXml == null) {
            return CacheCommand.DELETE;
        }
        // if package.xml was changed, change it in the package.
        if(!newXml.equals(pkg.getPackageXml())) {
            pkg.setPackageXml(newXml);
        }
        // 2. check for new packets in root & apply changes
        pkg.setPackets(findPacketFiles(pkg.getRoots()[0]));
        // 3. check new name of PSI directory (XML in the future. if renamed -> set RENAME and continue)
        if(!pkg.getRoots()[0].getName().equals(pkg.getName())) { // change to XML eventually
            pkg.setName(pkg.getRoots()[0].getName());
            return CacheCommand.RENAME;
        } else {
            return CacheCommand.NONE;
        }
    }

    private boolean notInProject(Project project, VirtualFile vFile) {
        return !ProjectRootManager.getInstance(project).getFileIndex().isInContent(vFile);
    }

    private boolean belongsToProject(VFileEvent event, Project project) {
        return ProjectRootManager.getInstance(project).getFileIndex().isInContent(ROSPackageUtil.getParentOfEvent(event));
    }

    private void sortEventsByPkgRoot(@NotNull List<VFileEvent> projectEvents,
                                     @NotNull MultiMap<ROSPackage, VFileEvent> map) {
        projectEvents.parallelStream().forEach(event -> sortSingleEvent(event, map));
    }

    private void sortSingleEvent(@NotNull VFileEvent event, @NotNull MultiMap<ROSPackage, VFileEvent> map) {
        if(map.containsScalarValue(event)) {
            return;
        }
        for (ROSPackage pkg : map.keySet()) {
            for (PsiDirectory root : pkg.getRoots()) {
                if (ROSPackageUtil.belongsToRoot(root, event)) {
                    map.putValue(pkg, event);
                    return;
                }
            }
        }
    }

    @Contract("_, _, _, _, _ -> new")
    @NotNull
    private ROSPackage createROSPackage(Project project, String name, PsiDirectory root,
                                        XmlFile pkgXml, List<ROSPktFile> packets) {
        return new ROSSourcePackage(project, name, root, pkgXml, packets);
    }

    private List<ROSPktFile> findPacketFiles(@NotNull PsiDirectory pkgRoot) {
        List<ROSPktFile> ret = new SortedList<>(Comparator.comparing(ROSPktFile::getQualifiedName));
        for (PsiFile file : pkgRoot.getFiles()) {
            if (file.getLanguage() == ROSPktLanguage.INSTANCE) {
                ret.add((ROSPktFile) file);
            }
        }
        for (PsiDirectory subDir : pkgRoot.getSubdirectories()) {
            ret.addAll(findPacketFiles(subDir));
        }
        return ret;
    }
}
