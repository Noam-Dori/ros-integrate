package ros.integrate.pkg;

import com.intellij.ide.highlighter.ModuleFileType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectBundle;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.pkg.psi.impl.ROSSourcePackage;
import ros.integrate.pkt.psi.ROSPktFile;
import ros.integrate.settings.ROSSettings;

import java.io.File;
import java.util.*;

/**
 * a finder used to find packages in the project's workspace. These packages do not need to be inside the project to be found.
 */
public class ROSWorkspacePackageFinder extends ROSPackageFinderBase {
    private static final Logger LOG = Logger.getInstance("#ros.integrate.workspace.ROSWorkspacePackageFinder");
    private final String MODULE_NAME = "workspace";

    private Module loadedModule = null;
    private Module getModule(Project project) {
        return loadedModule == null ? ModuleManager.getInstance(project).findModuleByName(MODULE_NAME) : loadedModule;
    }

    @NotNull
    private List<VirtualFile> getWorkspaceRoots(Project project) {
        Module origin = getModule(project);
        Objects.requireNonNull(origin);
        ModuleRootManager manager = ModuleRootManager.getInstance(origin);
        return Arrays.asList(manager.getContentRoots());
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
        return Optional.ofNullable(getModule(project))
                .map(Module::getModuleContentScope).orElse(GlobalSearchScope.EMPTY_SCOPE);
    }

    @NotNull
    @Override
    public Set<Module> loadArtifacts(Project project) {
        ModifiableModuleModel moduleModel = ModuleManager.getInstance(project).getModifiableModel();
        Module module = moduleModel.findModuleByName(MODULE_NAME);
        if (module != null) {
            moduleModel.disposeModule(module);
        }
        try {
            module = ModuleManager.getInstance(project).newModule(project.getBasePath() + File.separator +
                            Project.DIRECTORY_STORE_FOLDER + File.separator + MODULE_NAME +
                            ModuleFileType.DOT_DEFAULT_EXTENSION, WorkspaceModuleType.getInstance().getId());
            loadedModule = module;
        } catch (Exception e) {
            Messages.showErrorDialog(ProjectBundle.message("module.add.error.message", e.getMessage()),
                    ProjectBundle.message("module.add.error.title"));
            return Collections.emptySet();
        }
        ModifiableRootModel rootModel = ModuleRootManager.getInstance(module).getModifiableModel();

        ROSSettings settings = ROSSettings.getInstance(project);
        Set<String> paths = new HashSet<>(settings.getAdditionalSources());
        if (!settings.getWorkspacePath().isEmpty()) {
            paths.add(settings.getWorkspacePath());
        }
        for (String path : paths) {
            VirtualFile root = VirtualFileManager.getInstance()
                    .findFileByUrl(VirtualFileManager.constructUrl(LocalFileSystem.PROTOCOL, path));
            if (root != null) {
                rootModel.addContentEntry(root);
            }
        }
        rootModel.commit();
        return Collections.singleton(module);
    }

    @Override
    public boolean updateArtifacts(Project project) {
        Module module = getModule(project);
        SetDifference<String> changes = checkUrlChanges(project, module);

        if (changes.areEqual()) {
            return false;
        } else {
            ModifiableRootModel model = ModuleRootManager.getInstance(module).getModifiableModel();
            for (String addUrl : changes.entriesOnlyOnLeft()) {
                VirtualFile root = VirtualFileManager.getInstance().findFileByUrl(addUrl);
                if (root != null) {
                    model.addContentEntry(root);
                }
            }
            ContentEntry[] entries =  model.getContentEntries();
            for (String removeUrl : changes.entriesOnlyOnRight()) {
                for (ContentEntry entry : entries) {
                    if (entry.getUrl().equals(removeUrl)) {
                        model.removeContentEntry(entry);
                        break;
                    }
                }
            }
            model.commit();
            return true;
        }
    }

    @Override
    public void setDependency(Module module) {
        ModifiableRootModel model = ModuleRootManager.getInstance(module).getModifiableModel();
        Module workspaceModule = getModule(module.getProject());
        if (workspaceModule == null) {
            return;
        }
        ModuleOrderEntry entry = model.findModuleOrderEntry(workspaceModule);
        if (entry == null) {
            ModuleRootModificationUtil.addDependency(module, workspaceModule);
        }
        model.dispose();
    }

    @NotNull
    private SetDifference<String> checkUrlChanges(Project project, Module moduleToCheck) {
        ROSSettings settings = ROSSettings.getInstance(project);
        Set<String> newUrls = new HashSet<>(), oldUrls = new HashSet<>();

        settings.getAdditionalSources().stream()
                .map(path -> VirtualFileManager.constructUrl(LocalFileSystem.PROTOCOL, path))
                .forEach(newUrls::add);
        newUrls.add(VirtualFileManager.constructUrl(LocalFileSystem.PROTOCOL,
                settings.getWorkspacePath()));

        Collections.addAll(oldUrls, ModuleRootManager.getInstance(moduleToCheck).getContentRootUrls());

        return SetDifference.difference(newUrls, oldUrls);
    }

    boolean notInFinder(@NotNull VirtualFile vFile, @NotNull Project project) {
        return getWorkspaceRoots(project).stream().noneMatch(root -> ROSPackageUtil.belongsToRoot(root, vFile));
    }

    boolean inFinder(@NotNull VFileEvent event, @NotNull Project project) {
        return getWorkspaceRoots(project).stream().anyMatch(root -> ROSPackageUtil.belongsToRoot(root, event));
    }
}
