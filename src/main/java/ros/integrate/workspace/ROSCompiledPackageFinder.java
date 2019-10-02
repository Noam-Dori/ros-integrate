package ros.integrate.workspace;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.impl.scopes.LibraryScope;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
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
import ros.integrate.workspace.psi.impl.ROSCompiledPackage;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static ros.integrate.workspace.psi.impl.ROSCompiledPackage.*;

/**
 * a default finder used for finding compiled packages within the libraries
 */
public class ROSCompiledPackageFinder implements ROSPackageFinder {
    private static final Logger LOG = Logger.getInstance("#ros.integrate.workspace.ROSCompiledPackageFinder");

    @Override
    public void findAndCache(Project project, ConcurrentMap<String, ROSPackage> pkgCache) {
        /*
         * the files that actually determine whether or not a directory is a package is the package.xml file.
         * the following condition is true:
         * A directory is a ROS package iff one of its roots directly contains a package.xml file
         */
        GlobalSearchScope scope = Optional.ofNullable(LibraryTablesRegistrar.getInstance()
                .getLibraryTable(project).getLibraryByName("ROS"))
                .map(lib -> (GlobalSearchScope) new LibraryScope(project, lib)).orElse(GlobalSearchScope.EMPTY_SCOPE);
        FileTypeIndex.getFiles(XmlFileType.INSTANCE, scope)
                .stream().filter(xml -> xml.getName().equals(ROSPackageUtil.PACKAGE_XML))
                .forEach(vXml -> {
                    ROSPackage newPkg = investigateXml(vXml, project, pkgCache);
                    if (newPkg != ROSPackage.ORPHAN) {
                        pkgCache.putIfAbsent(newPkg.getName(), newPkg);
                    }
                });
    }

    private VirtualFile getROSRoot(Project project) {
        Library origin = LibraryTablesRegistrar.getInstance().getLibraryTable(project).getLibraryByName("ROS");
        return Objects.requireNonNull(origin).getFiles(OrderRootType.SOURCES)[0];
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
        PsiDirectory shareRoot = pkgXml.getContainingDirectory();
        // 4.5. TODO other roots of compiled packages: lib,include,bin(?),etc
        // 5. TODO try getting CMakeLists.txt since this is a project package
        // 6. find all packet files
        // FIXME for now they will just be searched in the project regardless of CMakeLists.txt
        List<ROSPktFile> pkgPackets = findPacketFiles(shareRoot/*,pkgCMake*/);
        // 7. make package and cache it. MAKE SURE IT IS NOT ROSPackage.ORPHAN

        Map<RootType,PsiDirectory> rootMap = new HashMap<>();
        rootMap.put(RootType.SHARE,shareRoot);
        ROSPackage newPkg = createROSPackage(project, pkgName, rootMap , pkgXml, pkgPackets);
        if (newPkg == ROSPackage.ORPHAN) {
            LOG.error("Failed indexing a valid ROS package",
                    "the compiled package finder tried finding a ros package, and failed.",
                    "Name: [" + pkgName + "]",
                    "Share Root: [" + shareRoot.getVirtualFile().getPath() + "]");
        }
        return newPkg;
    }

    @Override
    public MultiMap<ROSPackage, VFileEvent> investigate(@NotNull Project project, @NotNull Collection<VFileEvent> events) {
        MultiMap<ROSPackage, VFileEvent> ret = new MultiMap<>();
        // 1. check who is under the jurisdiction of this finder
        List<VFileEvent> libraryEvents = events.stream()
                .filter(event -> inROSLibrary(event, project)).collect(Collectors.toList());
        // 2. do XML parents first and get a list of parent directories, use these to create ROSCompiledPackages using original method.
        libraryEvents.stream().filter(ROSPackageUtil::isPackageXml).forEach(event -> {
            ROSPackage newPkg = investigateXml(Objects.requireNonNull(ROSPackageUtil.getXml(event)),
                    project, null);
            if(newPkg != ROSPackage.ORPHAN) {
                ret.putValue(newPkg, event);
            }
        });
        sortEventsByPkgRoot(libraryEvents, ret);
        return ret;
    }

    @Override
    public CacheCommand investigateChanges(Project project, ROSPackage pkg) {
        // 0. check the package is under the jurisdiction of this finder.
        if(!(pkg instanceof ROSCompiledPackage)) {
            return null;
        } // this has one sole root.
        if(notInROSLibrary(pkg.getRoots()[0].getVirtualFile(), project)) { // something is up with the root
            if(pkg.getRoots()[0].getParentDirectory() != null && // check parent - if its in the project, the dir was deleted.
                    notInROSLibrary(pkg.getRoots()[0].getParentDirectory().getVirtualFile(), project)) {
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
            String newName = pkg.getRoots()[0].getName();
            if(!pkg.getName().equals(newName)) {
                pkg.setName(newName);
            }
            return CacheCommand.RENAME;
        } else {
            return CacheCommand.NONE;
        }
    }

    private boolean notInROSLibrary(@NotNull VirtualFile vFile, Project project) {
        return !vFile.getPath().contains(getROSRoot(project).getPath());
    }

    private boolean inROSLibrary(VFileEvent event, Project project) {
         return ROSPackageUtil.belongsToRoot(getROSRoot(project),event);
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
    private ROSPackage createROSPackage(Project project, String name, Map<RootType, PsiDirectory> rootMap,
                                        XmlFile pkgXml, List<ROSPktFile> packets) {
        return new ROSCompiledPackage(project, name, rootMap, pkgXml, packets);
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
