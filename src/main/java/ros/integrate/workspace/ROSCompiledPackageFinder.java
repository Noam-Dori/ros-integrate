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
import ros.integrate.pkt.psi.ROSPktFile;
import ros.integrate.settings.ROSSettings;
import ros.integrate.workspace.psi.ROSPackage;
import ros.integrate.workspace.psi.impl.ROSCompiledPackage;

import java.util.*;

import static ros.integrate.workspace.psi.impl.ROSCompiledPackage.RootType;

/**
 * a default finder used for finding compiled packages within the libraries
 */
public class ROSCompiledPackageFinder extends ROSPackageFinderBase {
    private static final Logger LOG = Logger.getInstance("#ros.integrate.workspace.ROSCompiledPackageFinder");

    private VirtualFile getROSRoot(Project project) {
        Library origin = LibraryTablesRegistrar.getInstance().getLibraryTable(project).getLibraryByName("ROS");
        return Objects.requireNonNull(origin).getFiles(OrderRootType.SOURCES)[0];
    }

    @Override
    Class<? extends ROSPackage> getPackageType() {
        return ROSCompiledPackage.class;
    }

    @NotNull
    @Override
    ROSPackage tryNewROSPackage(Project project, String pkgName, PsiDirectory xmlRoot, XmlFile pkgXml, List<ROSPktFile> pkgPackets) {
        Map<RootType, PsiDirectory> rootMap = new HashMap<>();
        rootMap.put(RootType.SHARE, xmlRoot);
        ROSPackage newPkg = new ROSCompiledPackage(project, pkgName, rootMap, pkgXml, pkgPackets);
        if (newPkg == ROSPackage.ORPHAN) {
            LOG.error("Failed indexing a valid ROS package",
                    "the compiled package finder tried finding a ros package, and failed.",
                    "Name: [" + pkgName + "]",
                    "Share Root: [" + xmlRoot.getVirtualFile().getPath() + "]");
        }
        return newPkg;
    }

    @NotNull
    @Override
    GlobalSearchScope getScope(Project project) {
        return Optional.ofNullable(LibraryTablesRegistrar.getInstance()
                .getLibraryTable(project).getLibraryByName("ROS"))
                .map(lib -> (GlobalSearchScope) new LibraryScope(project, lib)).orElse(GlobalSearchScope.EMPTY_SCOPE);
    }

    @NotNull
    @Override
    public Library getLibrary(Project project) {
        String url = VirtualFileManager.constructUrl(LocalFileSystem.PROTOCOL,
                ROSSettings.getInstance(project).getROSPath());
        LibraryTable table = LibraryTablesRegistrar.getInstance().getLibraryTable(project);
        Library lib = table.getLibraryByName("ROS");
        if (lib != null) {
            table.removeLibrary(lib);
        }
        lib = table.createLibrary("ROS");
        Library.ModifiableModel model = lib.getModifiableModel();
        VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(url);
        if (file != null) {
            model.addRoot(file, OrderRootType.SOURCES);
            model.commit();
        }

        return lib;
    }

    boolean notInFinder(@NotNull VirtualFile vFile, @NotNull Project project) {
        return !vFile.getPath().contains(getROSRoot(project).getPath());
    }

    boolean inFinder(@NotNull VFileEvent event, @NotNull Project project) {
         return ROSPackageUtil.belongsToRoot(getROSRoot(project),event);
    }
}
