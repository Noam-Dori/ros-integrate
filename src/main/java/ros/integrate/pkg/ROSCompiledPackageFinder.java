package ros.integrate.pkg;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.impl.scopes.LibraryScope;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.*;
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
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.pkg.psi.impl.ROSCompiledPackage;

import java.util.*;
import java.util.function.BiPredicate;

import static ros.integrate.pkg.psi.impl.ROSCompiledPackage.RootType;

/**
 * a package finder that searches for packages in the installation path, (or ROS path).
 * It also adds the ROS library
 * @author Noam Dori
 */
public class ROSCompiledPackageFinder extends ROSPackageFinderBase {
    private static final Logger LOG = Logger.getInstance("#ros.integrate.workspace.ROSCompiledPackageFinder");
    private final Map<Project, Library> rosLib = new HashMap<>();
    private static final String ROS_LIB = "ROS";

    @NotNull
    private Library getLibrary(Project project) {
        return rosLib.computeIfAbsent(project, p -> {
            LibraryTable table = LibraryTablesRegistrar.getInstance().getLibraryTable(project);
            return Optional.ofNullable(table.getLibraryByName(ROS_LIB)).orElseGet(() -> table.createLibrary(ROS_LIB));
        });
    }

    @Nullable
    private VirtualFile getROSRoot(Project project) {
        Library origin = getLibrary(project);
        VirtualFile[] ret = Objects.requireNonNull(origin).getFiles(OrderRootType.CLASSES);
        return ret.length > 0 ? ret[0] : null;
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
        return Optional.of(getLibrary(project)).map(lib -> (GlobalSearchScope) new LibraryScope(project, lib))
                .orElse(GlobalSearchScope.EMPTY_SCOPE);
    }

    @Override
    public void loadLibraries(Project project) {
        String rosPath = ROSSettings.getInstance(project).getROSPath();
        addPath(getLibrary(project), rosPath, (model, url) -> true);
    }

    @Override
    public boolean updateLibraries(Project project) {
        Library lib = getLibrary(project);
        return addPath(lib, ROSSettings.getInstance(project).getROSPath(), (model, newUrl) -> {
            if (!Arrays.asList(model.getUrls(OrderRootType.CLASSES)).contains(newUrl)) {
                Arrays.stream(model.getUrls(OrderRootType.CLASSES))
                        .forEach(modelUrl -> model.removeRoot(modelUrl, OrderRootType.CLASSES));
                return true;
            } else {
                return false;
            }
        });
    }

    @Override
    public void setDependency(@NotNull Module module) {
        Library lib = getLibrary(module.getProject());
        ModifiableRootModel model = ModuleRootManager.getInstance(module).getModifiableModel();
        LibraryOrderEntry entry = model.findLibraryOrderEntry(lib);
        if (entry == null) {
            ModuleRootModificationUtil.addDependency(module, lib);
        }
        model.dispose();
    }

    private boolean addPath(@NotNull Library lib, @NotNull String path,
                            @NotNull BiPredicate<Library.ModifiableModel, String> predicate) {
        Library.ModifiableModel model = lib.getModifiableModel();
        String url = VirtualFileManager.constructUrl(LocalFileSystem.PROTOCOL, path);
        if (!predicate.test(model, url)) {
            return false;
        }
        if (!path.isEmpty()) {
            VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(url);
            if (file != null) {
                // note: OrderRootType.SOURCES also works, but will not show in external libraries.
                model.addRoot(file, OrderRootType.CLASSES);
                model.commit();
            }
            return true;
        }
        return false;
    }

    boolean notInFinder(@NotNull VirtualFile vFile, @NotNull Project project) {
        VirtualFile root = getROSRoot(project);
        return root == null || !ROSPackageUtil.belongsToRoot(root, vFile);
    }

    boolean inFinder(@NotNull VFileEvent event, @NotNull Project project) {
        VirtualFile root = getROSRoot(project);
        return root != null && ROSPackageUtil.belongsToRoot(root, event);
    }
}
