package ros.integrate.pkg;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScopesCore;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.pkg.psi.impl.ROSSourcePackage;
import ros.integrate.pkt.psi.ROSPktFile;
import ros.integrate.settings.ROSSettings;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * a package finder used to find packages in the project's workspace or additional package paths.
 * These packages do not need to be inside the project to be found.
 * Moreover, this finder also adds the workspace library which has a bunch of roots loading to all workspace packages
 * that are not contained in the project
 * @author Noam Dori
 */
public class ROSWorkspacePackageFinder extends ROSPackageFinderBase {
    private static final Logger LOG = Logger.getInstance("#ros.integrate.workspace.ROSWorkspacePackageFinder");
    private static final String WS_LIB = "workspace";
    private static final VirtualFileSystem FILE_SYSTEM = VirtualFileManager.getInstance()
            .getFileSystem(LocalFileSystem.PROTOCOL);
    private final Map<Project, String> wsPath = new HashMap<>(1);

    @Nullable
    private VirtualFile toVirtualFile(@Nullable String path) {
        if (path == null) {
            return null;
        }
        return FILE_SYSTEM.findFileByPath(path);
    }

    @Nullable
    private String getWorkspacePath(@NotNull Project project) {
        return wsPath.putIfAbsent(project, ROSSettings.getInstance(project).getWorkspacePath());
    }

    @NotNull
    private List<VirtualFile> getWorkspaceRoots(Project project) {
        List<VirtualFile> ret = new ArrayList<>(Arrays.asList(getLibrary(project).getFiles(OrderRootType.CLASSES)));
        Optional.ofNullable(toVirtualFile(getWorkspacePath(project))).ifPresent(ret::add);
        return ret;
    }

    @NotNull
    private Library getLibrary(Project project) {
        LibraryTable table = LibraryTablesRegistrar.getInstance().getLibraryTable(project);
        return Optional.ofNullable(table.getLibraryByName(WS_LIB)).orElseGet(() -> table.createLibrary(WS_LIB));
    }

    @Override
    Class<? extends ROSPackage> getPackageType() {
        return ROSSourcePackage.class;
    }

    @NotNull
    @Override
    ROSPackage tryNewROSPackage(Project project, String pkgName, PsiDirectory xmlRoot, XmlFile pkgXml, List<ROSPktFile> pkgPackets) {
        ROSPackage newPkg = new ROSSourcePackage(project, pkgName, xmlRoot, pkgXml, pkgPackets);
        if (newPkg.equals(ROSPackage.ORPHAN)) {
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
        return GlobalSearchScope.union(getWorkspaceRoots(project).stream()
                .map(vFile -> GlobalSearchScopesCore.directoryScope(project, vFile, true))
                .toArray(GlobalSearchScope[]::new));
    }

    @Override
    public void loadLibraries(Project project) {
        ROSSettings settings = ROSSettings.getInstance(project);
        Set<VirtualFile> files = settings.getAdditionalSources().stream().map(this::toVirtualFile)
                .collect(Collectors.toSet());
        Optional.ofNullable(settings.getWorkspacePath())
                .map(peek(path -> wsPath.put(project, path)))
                .map(this::toVirtualFile)
                .map(root -> root.findChild("src"))
                .map(VirtualFile::getChildren)
                .ifPresent(children -> Collections.addAll(files, children));
        getWorkspaceRoots(project).forEach(files::remove);
        GlobalSearchScope projectScope = GlobalSearchScope.projectScope(project);
        Library.ModifiableModel model = getLibrary(project).getModifiableModel();
        for (VirtualFile file : files) {
            if (file != null && !projectScope.contains(file)) {
                model.addRoot(file, OrderRootType.CLASSES);
            }
        }
        model.commit();
    }

    @Override
    public boolean updateLibraries(Project project) {
        SetDifference<VirtualFile> changes = checkUrlChanges(project);

        if (changes.areEqual()) {
            return false;
        } else {
            Library.ModifiableModel model = getLibrary(project).getModifiableModel();
            for (VirtualFile addFile : changes.entriesOnlyOnLeft()) {
                model.addRoot(addFile, OrderRootType.CLASSES);
            }
            for (VirtualFile removeFile : changes.entriesOnlyOnRight()) {
                model.removeRoot(removeFile.getUrl(), OrderRootType.CLASSES);
            }
            model.commit();
            return true;
        }
    }

    @Override
    public void setDependency(Module module) {
        ModifiableRootModel model = ModuleRootManager.getInstance(module).getModifiableModel();
        LibraryOrderEntry entry = model.findLibraryOrderEntry(getLibrary(module.getProject()));
        if (entry == null) {
            ModuleRootModificationUtil.addDependency(module, getLibrary(module.getProject()));
        }
        model.dispose();
    }

    @NotNull
    private SetDifference<VirtualFile> checkUrlChanges(Project project) {
        ROSSettings settings = ROSSettings.getInstance(project);
        Set<VirtualFile> oldFiles = new HashSet<>(getWorkspaceRoots(project)),
                newFiles = settings.getAdditionalSources().stream().map(this::toVirtualFile)
                        .collect(Collectors.toSet());
        Optional.ofNullable(settings.getWorkspacePath())
                .map(path -> wsPath.put(project, path)) // this update will help the scope that uses it.
                .map(this::toVirtualFile)
                .map(root -> root.findChild("src"))
                .map(VirtualFile::getChildren)
                .ifPresent(children -> Collections.addAll(newFiles, children));
        GlobalSearchScope projectScope = GlobalSearchScope.projectScope(project);
        newFiles.removeIf(file -> file == null || projectScope.contains(file));
        return SetDifference.difference(newFiles, oldFiles);
    }

    boolean notInFinder(@NotNull VirtualFile vFile, @NotNull Project project) {
        return getWorkspaceRoots(project).stream().noneMatch(root -> ROSPackageUtil.belongsToRoot(root, vFile));
    }

    boolean inFinder(@NotNull VFileEvent event, @NotNull Project project) {
        return getWorkspaceRoots(project).stream().anyMatch(root -> ROSPackageUtil.belongsToRoot(root, event));
    }

    @Contract(pure = true)
    private static <T, R> @NotNull Function<T, T> peek(Function<T, R> f) {
        return t -> {f.apply(t); return t;};
    }
}
