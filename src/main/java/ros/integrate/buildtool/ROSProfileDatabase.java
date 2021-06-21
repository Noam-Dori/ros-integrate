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
import com.intellij.util.xmlb.annotations.Tag;
import com.intellij.util.xmlb.annotations.XMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;
import ros.integrate.settings.ROSSettings;
import ros.integrate.ui.PathListUtil;

import java.io.IOException;
import java.util.*;

@State(name = "ROSProfileDatabase", storages = @Storage("profiles.xml"))
public class ROSProfileDatabase implements PersistentStateComponent<ROSProfileDatabase.State> {
    @Tag("profile")
    static class ProfileState {
        @XMap
        public Map<String, String> profile = new HashMap<>();

        public ProfileState(Map<String, String> data) {
            profile.putAll(data);
        }

        @SuppressWarnings("unused")
        public ProfileState() { }
    }

    static class State {
        public Map<ROSBuildTool, List<ProfileState>> data;

        State() {
            data = new HashMap<>();
            for (ROSBuildTool buildTool : ROSBuildTool.values()) {
                data.put(buildTool, new ArrayList<>());
            }
        }
    }

    private static final Logger LOG = Logger.getInstance("#ros.integrate.buildtool.ROSProfileDatabase");
    @NotNull
    private final ROSSettings settings;
    @NotNull
    private final State state = new State();
    @NotNull
    private static final VirtualFileSystem FILE_SYSTEM = VirtualFileManager.getInstance()
            .getFileSystem(LocalFileSystem.PROTOCOL);

    private ROSProfileDatabase(Project project) {
        settings = ROSSettings.getInstance(project);
    }

    @Override
    public @Nullable State getState() {
        LOG.info("saving Profile state");
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        LOG.info("loading Profile state");
        this.state.data.forEach((key, value) -> {
            value.clear();
            value.addAll(state.data.get(key));
        });
    }

    @NotNull
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


    public void removeProfile(@NotNull ROSProfile profile) throws IOException {
        state.data.get(profile.getBuildtool())
                .removeIf(dataProfile -> profile.getName().equals(dataProfile.profile.get("name")));
        if (profile.getBuildtool() == ROSBuildTool.CATKIN_TOOLS) {
            VirtualFile profilesDir = FILE_SYSTEM.findFileByPath(settings.getWorkspacePath() + "/.catkin_tools/profiles");
            if (profilesDir == null) {
                return;
            }
            for (VirtualFile profileDir : profilesDir.getChildren()) {
                if (profileDir.getName().equals(profile.getName())) {
                    profileDir.delete(null);
                }
            }
        }
    }

    public void updateProfile(@Nullable ROSProfile oldProfile, @NotNull ROSProfile newProfile) {
        if (newProfile.getBuildtool() == ROSBuildTool.CATKIN_TOOLS) {
            VirtualFile profilesDir = FILE_SYSTEM.findFileByPath(settings.getWorkspacePath() + "/.catkin_tools/profiles");
            if (profilesDir == null) {
                return;
            }
            for (VirtualFile profileDir : profilesDir.getChildren()) {
                if (profileDir.getName().equals(newProfile.getName())) {
                    Yaml config = new Yaml();
                    VirtualFile configFile = Optional.ofNullable(profileDir.findChild("config.yaml"))
                            .orElseGet(() -> profileDir.findChild("build.yaml"));
                    // TODO: 6/20/2021 update yaml files
                    break;
                }
            }
        } else {
            if (oldProfile == null) {
                state.data.get(newProfile.getBuildtool()).add(new ProfileState(newProfile.getRawData()));
            }
            else if (state.data.get(newProfile.getBuildtool()).stream()
                        .filter(data -> oldProfile.getName().equals(data.profile.get("name")))
                        .peek(old -> {
                            old.profile.clear();
                            newProfile.save();
                            old.profile.putAll(new ProfileState(newProfile.getRawData()).profile);
                        }).count() == 0) {
                state.data.get(newProfile.getBuildtool()).add(new ProfileState(newProfile.getRawData()));
            }
        }
    }

    @NotNull
    private List<ROSProfile> loadColcon() {
        // load existing profiles from persistent state component.
        List<ROSProfile> ret = new ArrayList<>();
        List<ProfileState> stateData = state.data.get(ROSBuildTool.COLCON);
        // if there are no profiles in the XML, and the .catkin_workspace file exists, load that as a profile.
        if (stateData.isEmpty()) {
            // find out if colcon is loaded
            String defaultPath = System.getenv("COLCON_DEFAULTS_FILE");
            if (defaultPath == null) {
                defaultPath = System.getenv("COLCON_HOME");
                if (defaultPath == null) {
                    return Collections.emptyList();
                }
                defaultPath += "/defaults.yaml";
            }
            VirtualFile colconDefaults = FILE_SYSTEM.findFileByPath(defaultPath);
            if (colconDefaults == null) {
                return Collections.emptyList();
            }
            Yaml config = new Yaml();
            try {
                Map<String, Object> data = config.load(colconDefaults.getInputStream());
                ROSProfile profile = new ROSProfile();
                profile.setGuiName("colcon_defaults");
                profile.setGuiBuildtool(ROSBuildTool.COLCON);
                profile.setIsolation(true);

                @SuppressWarnings("unchecked")
                Map<String, Object> buildVerb = (Map<String, Object>) data.get("build");
                if (buildVerb != null) {
                    profile.setBuildDirectory(extendPath(buildVerb, "build-base", "build"));
                    profile.setInstallDirectory(extendPath(buildVerb, "install-base", "install"));
                    profile.setCmakeArgs(serialize(buildVerb, "cmake-args", ' '));

                    profile.setDenyList(serialize(buildVerb, "packages-skip", ' '));
                    profile.setAllowList(serialize(buildVerb, "packages-select", ' '));
                    profile.setSourceDirectory(extendPath(getFirst(buildVerb, "paths", "src")));
                }
                profile.setInstall(true);

                profile.save();
                ret.add(profile);
                stateData.add(new ProfileState(profile.getRawData()));
            } catch (IOException e) {
                LOG.error(String.format("Read of file [%s] failed with exception", colconDefaults.getPath()), e);
            }
        } else {
            stateData.forEach(raw -> ret.add(ROSProfile.fromRawData(raw.profile)));
        }
        return ret;
    }

    @NotNull
    private List<ROSProfile> loadCatkinMake() {
        // load existing profiles from persistent state component.
        List<ROSProfile> ret = new ArrayList<>();
        List<ProfileState> stateData = state.data.get(ROSBuildTool.CATKIN_MAKE);
        // if there are no profiles in the XML, and the .catkin_workspace file exists, load that as a profile.
        if (stateData.isEmpty() && FILE_SYSTEM.findFileByPath(settings.getWorkspacePath() + "/.catkin_workspace") != null) {
            ROSProfile profile = new ROSProfile();
            profile.setGuiName("catkin_workspace");
            profile.setGuiBuildtool(ROSBuildTool.CATKIN_MAKE);
            profile.setInstall(false);
            profile.setIsolation(true);
            profile.setBuildDirectory(extendPath( "build"));
            profile.setDevelDirectory(extendPath( "devel"));
            profile.setSourceDirectory(extendPath("src"));
            profile.setInstallDirectory(extendPath("install"));
            profile.save();
            ret.add(profile);
            stateData.add(new ProfileState(profile.getRawData()));
        } else {
            stateData.forEach(raw -> ret.add(ROSProfile.fromRawData(raw.profile)));
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
                profile.setBuildDirectory(extendPath(data, "build_space", "build"));
                profile.setDevelDirectory(extendPath(data, "devel_space", "devel"));
                profile.setSourceDirectory(extendPath(data, "source_space", "src"));
                profile.setInstallDirectory(extendPath(data, "install_space", "install"));
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
        Object list = data.get(lookupKey);
        if (list instanceof List<?>) {
            return PathListUtil.serializePathList((List<String>)
                    data.getOrDefault(lookupKey, Collections.emptyList()), delimiter);
        } else {
            return (String) data.getOrDefault(lookupKey, "");
        }
    }

    private String extendPath(@NotNull Map<String, Object> data, String lookupKey, String def) {
        String raw = (String) data.getOrDefault(lookupKey, def);
        return raw.startsWith("/") ? raw : settings.getWorkspacePath() + "/" + raw;
    }

    private String extendPath(@NotNull String raw) {
        return raw.startsWith("/") ? raw : settings.getWorkspacePath() + "/" + raw;
    }

    @SuppressWarnings("SameParameterValue")
    private String getFirst(@NotNull Map<String, Object> data, String lookupKey, String def) {
        Object ret = data.getOrDefault(lookupKey, def);
        if (ret instanceof List<?>) {
            return (String) ((List<?>) ret).get(0);
        } else {
            return (String) ret;
        }
    }
}
