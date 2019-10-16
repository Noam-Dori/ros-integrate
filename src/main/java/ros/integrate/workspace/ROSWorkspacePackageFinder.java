package ros.integrate.workspace;

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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * a default finder used for finding packages within the project
 */
public class ROSWorkspacePackageFinder extends ROSPackageFinderBase {
    private static final Logger LOG = Logger.getInstance("#ros.integrate.workspace.ROSProjectPackageFinder");

    private VirtualFile getWorkspaceRoot(Project project) {
        Library origin = LibraryTablesRegistrar.getInstance().getLibraryTable(project).getLibraryByName("workspace");
        return Objects.requireNonNull(origin).getFiles(OrderRootType.SOURCES)[0];
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
        String url = VirtualFileManager.constructUrl(LocalFileSystem.PROTOCOL,
                ROSSettings.getInstance(project).getWorkspacePath()); // TODO additional sources via $ROS_PACKAGE_PATH
        LibraryTable table = LibraryTablesRegistrar.getInstance().getLibraryTable(project);
        Library lib = table.getLibraryByName("workspace");
        if (lib != null) {
            table.removeLibrary(lib);
        }
        lib = table.createLibrary("workspace");
        Library.ModifiableModel model = lib.getModifiableModel();
        VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(url);
        if (file != null) {
            model.addRoot(file, OrderRootType.SOURCES);
            model.commit();
        }

        return lib;
    }

    @Override
    public boolean updateLibrary(Project project, @NotNull Library lib) {
        Library.ModifiableModel model = lib.getModifiableModel();
        String newUrl = VirtualFileManager.constructUrl(LocalFileSystem.PROTOCOL,
                ROSSettings.getInstance(project).getWorkspacePath());
        if(!Arrays.asList(model.getUrls(OrderRootType.SOURCES)).contains(newUrl)) {
            Arrays.stream(model.getUrls(OrderRootType.SOURCES))
                    .forEach(modelUrl -> model.removeRoot(modelUrl, OrderRootType.SOURCES));
            VirtualFile newFile = VirtualFileManager.getInstance().findFileByUrl(newUrl);
            if (newFile != null) {
                model.addRoot(newFile, OrderRootType.SOURCES);
                model.commit();
            }
            return true;
        } else {
            return false;
        }
    }

    boolean notInFinder(@NotNull VirtualFile vFile, @NotNull Project project) {
        return !vFile.getPath().contains(getWorkspaceRoot(project).getPath());
    }

    boolean inFinder(@NotNull VFileEvent event, @NotNull Project project) {
        return ROSPackageUtil.belongsToRoot(getWorkspaceRoot(project),event);
    }
}
