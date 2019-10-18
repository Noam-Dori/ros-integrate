package ros.integrate.workspace;

import com.google.common.collect.Lists;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.impl.scopes.LibraryScope;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.psi.ROSPktFile;
import ros.integrate.settings.ROSSettings;
import ros.integrate.workspace.psi.ROSPackage;
import ros.integrate.workspace.psi.impl.ROSSourcePackage;

import java.util.*;

/**
 * a finder used to find packages in the project's workspace. These packages do not need to be inside the project to be found.
 */
public class ROSWorkspacePackageFinder extends ROSPackageFinderBase {
    private static final Logger LOG = Logger.getInstance("#ros.integrate.workspace.ROSWorkspacePackageFinder");

    @NotNull
    private List<VirtualFile> getWorkspaceRoots(Project project) {
        Library origin = LibraryTablesRegistrar.getInstance().getLibraryTable(project).getLibraryByName("workspace");
        Objects.requireNonNull(origin);
        return Lists.asList(origin.getFiles(OrderRootType.SOURCES)[0],origin.getFiles(OrderRootType.CLASSES));
    }

    @Override
    Class<? extends ROSPackage> getPackageType() {
        return ROSSourcePackage.class;
    }

    @NotNull
    @Override
    ROSPackage tryNewROSPackage(Project project, String pkgName, PsiDirectory xmlRoot, XmlFile pkgXml, List<ROSPktFile> pkgPackets) {
        ROSPackage newPkg = new ROSSourcePackage(project, pkgName, xmlRoot, pkgXml, pkgPackets);
        if (newPkg == ROSPackage.ORPHAN) {
            LOG.error("Failed indexing a valid ROS package",
                    "the project package finder tried finding a ros package, and failed.",
                    "Name: [" + pkgName + "]",
                    "Root: [" + xmlRoot.getVirtualFile().getPath() + "]");
        }
        return newPkg;
    }

    @NotNull
    @Override
    GlobalSearchScope getScope(Project project) {
        return Optional.ofNullable(LibraryTablesRegistrar.getInstance()
                .getLibraryTable(project).getLibraryByName("workspace"))
                .map(lib -> (GlobalSearchScope) new LibraryScope(project, lib)).orElse(GlobalSearchScope.EMPTY_SCOPE);
    }

    @Nullable
    @Override
    public Library getLibrary(Project project) {
        LibraryTable table = LibraryTablesRegistrar.getInstance().getLibraryTable(project);
        Library lib = table.getLibraryByName("workspace");
        if (lib != null) {
            table.removeLibrary(lib);
        }
        lib = table.createLibrary("workspace");
        Library.ModifiableModel model = lib.getModifiableModel();

        ROSSettings settings = ROSSettings.getInstance(project);
        Map<String,OrderRootType> paths = new HashMap<>();
        settings.getAdditionalSources().forEach(path -> paths.put(path,OrderRootType.CLASSES));
        paths.put(settings.getWorkspacePath(),OrderRootType.SOURCES);
        for (Map.Entry<String, OrderRootType> entry : paths.entrySet()) {
            VirtualFile root = VirtualFileManager.getInstance()
                    .findFileByUrl(VirtualFileManager.constructUrl(LocalFileSystem.PROTOCOL,entry.getKey()));
            if (root != null) {
                model.addRoot(root,entry.getValue());
            }
        }
        model.commit();
        return lib;
    }

    @Override
    public boolean updateLibrary(Project project, @NotNull Library lib) {
        MapDifference<String,OrderRootType> changes = checkUrlChanges(project,lib);

        if (changes.areEqual()) {
            return false;
        } else {
            Library.ModifiableModel model = lib.getModifiableModel();
            for (Map.Entry<String, OrderRootType> entry : changes.entriesOnlyOnLeft().entrySet()) {
                VirtualFile root = VirtualFileManager.getInstance().findFileByUrl(entry.getKey());
                if (root != null) {
                    model.addRoot(root, entry.getValue());
                }
            }
            for (Map.Entry<String, OrderRootType> entry : changes.entriesOnlyOnRight().entrySet()) {
                model.removeRoot(entry.getKey(), entry.getValue());
            }
            model.commit();
            return true;
        }
    }

    @NotNull
    private MapDifference<String, OrderRootType> checkUrlChanges(Project project, Library libraryToCheck) {
        ROSSettings settings = ROSSettings.getInstance(project);
        Map<String,OrderRootType> newUrls = new HashMap<>(), oldUrls = new HashMap<>();

        settings.getAdditionalSources().stream()
                .map(path -> VirtualFileManager.constructUrl(LocalFileSystem.PROTOCOL, path))
                .forEach(url -> newUrls.put(url,OrderRootType.CLASSES));
        newUrls.put(VirtualFileManager.constructUrl(LocalFileSystem.PROTOCOL,
                settings.getWorkspacePath()),OrderRootType.SOURCES);

        for (OrderRootType type : OrderRootType.getAllPersistentTypes()) {
            for (String url : libraryToCheck.getUrls(type)) {
                oldUrls.put(url, type);
            }
        }

        return Maps.difference(newUrls,oldUrls);
    }

    boolean notInFinder(@NotNull VirtualFile vFile, @NotNull Project project) {
        return getWorkspaceRoots(project).stream().noneMatch(root -> vFile.getPath().contains(root.getPath()));
    }

    boolean inFinder(@NotNull VFileEvent event, @NotNull Project project) {
        return getWorkspaceRoots(project).stream().anyMatch(root -> ROSPackageUtil.belongsToRoot(root,event));
    }
}
