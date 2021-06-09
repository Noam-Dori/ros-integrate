package ros.integrate.buildtool;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileSystem;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;
import ros.integrate.settings.ROSSettings;
import ros.integrate.ui.PathListUtil;

import java.io.IOException;
import java.util.*;

public class ROSProfileLoader {
    @State(name = "CatkinMakeProfiles",storages = @Storage("catkin_make.xml"))
    static class CatkinMakeProfiles implements PersistentStateComponent<List<ROSProfile>> {
        private final List<ROSProfile> data = new ArrayList<>();

        @Override
        public @NotNull List<ROSProfile> getState() {
            return data;
        }

        @Override
        public void loadState(@NotNull List<ROSProfile> state) {
            data.clear();
            data.addAll(state);
        }
    }

    private static final Logger LOG = Logger.getInstance("#ros.integrate.buildtool.ROSProfileLoader");
    @NotNull
    private final ROSSettings settings;
    @NotNull
    private final CatkinMakeProfiles catkinMakeProfiles;
    private static final VirtualFileSystem FILE_SYSTEM = VirtualFileManager.getInstance()
            .getFileSystem(LocalFileSystem.PROTOCOL);

    public ROSProfileLoader(Project project) {
        settings = ROSSettings.getInstance(project);
        catkinMakeProfiles = new CatkinMakeProfiles();
    }

    public List<ROSProfile> load(@NotNull ROSBuildTool buildTool) {
        if (settings.getWorkspacePath().isEmpty()) {
            return Collections.emptyList();
        }
            switch (buildTool) {
                case COLCON:
                    return loadColcon();
                case CATKIN_MAKE:
                    return loadCatkinMake();
                case CATKIN_TOOLS:
                    return loadCatkinTools();
            }
        return Collections.emptyList();
    }

    @NotNull
    private List<ROSProfile> loadColcon() {
        return Collections.emptyList();
    }

    @NotNull
    private List<ROSProfile> loadCatkinMake() {
        // load existing profiles from persistent state component.
        List<ROSProfile> ret = catkinMakeProfiles.getState();
        // if there are no profiles in the XML, and the .catkin_workspace file exists, load that as a profile.
        if (ret.isEmpty() && FILE_SYSTEM.findFileByPath(settings.getWorkspacePath() + "/.catkin_workspace") != null) {
            ROSProfile profile = new ROSProfile();
            profile.setGuiName("catkin_workspace");
            profile.setGuiBuildtool(ROSBuildTool.CATKIN_MAKE);
            profile.setInstall(false);
            profile.setIsolation(true);
            profile.setBuildDir(extendPath( "build"));
            profile.setDevelDir(extendPath( "devel"));
            profile.setSourceDir(extendPath("src"));
            profile.setInstallDir(extendPath("install"));
            profile.save();
            ret.add(profile);
        }
        return ret;
    }


    @NotNull
    private List<ROSProfile> loadCatkinTools() {
        VirtualFile profilesDir = FILE_SYSTEM.findFileByPath(settings.getWorkspacePath() + "/.catkin_tools/profiles");
        if (profilesDir == null) {
            return Collections.emptyList();
        }
        List<ROSProfile> ret = new ArrayList<>();
        for (VirtualFile profileDir : profilesDir.getChildren()) {
            Yaml config = new Yaml();
            VirtualFile configFile = Optional.ofNullable(profileDir.findChild("config.yaml"))
                    .orElseGet(() -> profileDir.findChild("build.yaml"));
            if (configFile == null) {
                continue;
            }
            try {
                Map<String, Object> data = config.load(configFile.getInputStream());
                ROSProfile profile = new ROSProfile();
                profile.setGuiBuildtool(ROSBuildTool.CATKIN_TOOLS);
                profile.setGuiName(profileDir.getName());
                profile.setInstall((Boolean) data.getOrDefault("install", true));
                profile.setIsolation(true);
                profile.setMakeArgs(serialize(data, "make_args", ' '));
                profile.setCmakeArgs(serialize(data, "cmake_args", ' '));
                profile.setBuildtoolArgs(serialize(data, "catkin_make_args", ' '));
                profile.setDenyList(serialize(data, "blacklist", ','));
                profile.setAllowList(serialize(data, "whitelist", ','));
                profile.setBuildDir(extendPath(data, "build_space", "build"));
                profile.setDevelDir(extendPath(data, "devel_space", "devel"));
                profile.setSourceDir(extendPath(data, "source_space", "src"));
                profile.setInstallDir(extendPath(data, "install_space", "install"));
                profile.save();
                ret.add(profile);
            } catch (IOException e) {
                LOG.error(String.format("Read of file [%s] failed with exception", configFile.getPath()), e);
            }
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    private String serialize(@NotNull Map<String, Object> data, String lookupKey, char delimiter) {
        return PathListUtil.serializePathList((List<String>)
                data.getOrDefault(lookupKey, Collections.emptyList()), delimiter);
    }

    private String extendPath(@NotNull Map<String, Object> data, String lookupKey, String def) {
        String raw = (String) data.getOrDefault(lookupKey, def);
        return raw.startsWith("/") ? raw : settings.getWorkspacePath() + "/" + raw;
    }

    private String extendPath(@NotNull String raw) {
        return raw.startsWith("/") ? raw : settings.getWorkspacePath() + "/" + raw;
    }
}
