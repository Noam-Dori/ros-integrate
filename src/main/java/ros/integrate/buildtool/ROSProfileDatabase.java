package ros.integrate.buildtool;

import com.intellij.openapi.application.WriteAction;
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
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import ros.integrate.settings.ROSSettings;
import ros.integrate.ui.PathListUtil;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

/**
 * the persistence layer of the profile model.
 * this service interacts with each buildtool's way to store profiles. This service does not store profiles,
 * only interacts with the files. If you want to interact with profiles, use {@link ROSProfiles}.
 * Some buildtools (for instance, {@link ROSBuildTool#CATKIN_MAKE}) do store any data about build configurations,
 * so the IDE takes care of that with a config file named <code>profiles.xml</code>
 * @author Noam Dori
 */
@State(name = "ROSProfileDatabase", storages = @Storage("profiles.xml"))
public class ROSProfileDatabase implements PersistentStateComponent<ROSProfileDatabase.State> {
    /**
     * a function that accepts 3 variables. nothing special.
     */
    interface TriConsumer<X1, X2, X3> {
        void accept(X1 x1, X2 x2, X3 x3);
    }

    /**
     * represents a profile from profiles.xml
     */
    @Tag("profile")
    static class ProfileState {
        @XMap
        public final Map<String, String> profile = new HashMap<>();

        public ProfileState(Map<String, String> data) {
            profile.putAll(data);
        }

        @SuppressWarnings("unused")
        public ProfileState() { }
    }

    /**
     * represents the contents of profiles.xml, which includes a map from the buildtool
     * to a list of all the raw profiles.
     */
    static class State {
        public final Map<ROSBuildTool, List<ProfileState>> data;

        State() {
            data = new HashMap<>();
            for (ROSBuildTool buildTool : ROSBuildTool.values()) {
                data.put(buildTool, new ArrayList<>());
            }
        }
    }

    @NotNull
    private static final Yaml YAML = getYaml();

    /**
     * since catkin_tools (and colcon) use yaml files to store data, this service has a yaml service to
     * interpret said data.
     * @return the YAML service with the needed options applied.
     */
    @NotNull
    private static Yaml getYaml() {
        DumperOptions options = new DumperOptions();
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return new Yaml(options);
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

    /**
     * load all profiles associated with a buildtool
     * @param buildTool the buildtool the loaded profiles should be associated with
     * @return a list of freshly loaded profiles.
     * @apiNote this is a heavy operation as it reads a lot of files. Use sparingly.
     */
    @NotNull
    public List<ROSProfile> load(@NotNull ROSBuildTool buildTool) {
        if (settings.getWorkspacePath().isEmpty()) {
            return Collections.emptyList();
        }
        return switch (buildTool) {
            case COLCON -> loadColcon();
            case CATKIN_MAKE -> loadCatkinMake();
            case CATKIN_TOOLS -> loadCatkinTools();
        };
    }

    /**
     * delete a profile.
     * @param profile the profile to remove.
     * @throws IOException if the write action could not be completed.
     */
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
                    WriteAction.run(() -> profileDir.delete(this));
                }
            }
        }
    }

    /**
     * update or add a new profile
     * @param oldProfile the old profile to change. Set to null if you are adding a new profile.
     * @param newProfile the new data of this profile (or the new profile). This profile should not run the "save" operation.
     * @throws IOException if the write action could not be completed.
     */
    public void updateProfile(@Nullable ROSProfile oldProfile, @NotNull ROSProfile newProfile) throws IOException {
        ROSBuildTool oldBuildTool = oldProfile == null ? null : oldProfile.getBuildtool();
        if (oldBuildTool != null && oldBuildTool != newProfile.getGuiBuildtool()) {
            removeProfile(oldProfile); // this runs if we are migrating a profile
        }
        if (newProfile.getGuiBuildtool() == ROSBuildTool.CATKIN_TOOLS) {
            VirtualFile profilesDir = FILE_SYSTEM.findFileByPath(settings.getWorkspacePath() + "/.catkin_tools/profiles");
            if (profilesDir == null) {
                return;
            }
            if (oldProfile == null) {
                newProfile.save();
                WriteAction.run(() -> {
                    VirtualFile profileDir = profilesDir.createChildDirectory(this, newProfile.getName());
                    VirtualFile configFile = profileDir.createChildData(this, "config.yaml");
                    Map<String, Object> data = extractCatkinToolsData(newProfile);
                    OutputStreamWriter configWriter = new OutputStreamWriter(configFile.getOutputStream(this));
                    YAML.dump(data, configWriter);
                    configWriter.close();
                });
            } else for (VirtualFile profileDir : profilesDir.getChildren()) {
                if (profileDir.getName().equals(oldProfile.getName())) {
                    newProfile.save();
                    WriteAction.run(() -> {
                        VirtualFile configFile = Optional.ofNullable(profileDir.findChild("config.yaml"))
                                .orElseGet(() -> profileDir.findChild("build.yaml"));
                        if (configFile == null) {
                            configFile = profileDir.createChildData(this, "config.yaml");
                        }
                        Map<String, Object> data = YAML.load(configFile.getInputStream());
                        data.putAll(extractCatkinToolsData(newProfile));
                        OutputStreamWriter configWriter = new OutputStreamWriter(configFile.getOutputStream(this));
                        YAML.dump(data, configWriter);
                        configWriter.close();
                        profileDir.rename(this, newProfile.getName());
                    });
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
    private Map<String, Object> extractCatkinToolsData(@NotNull ROSProfile newProfile) {
        Map<String, Object> ret = new HashMap<>();
        Map<String, String> raw = newProfile.getRawData();
        TriConsumer<String, String, Character> doPutArgs = (rawKey, yamlKey, c) -> {
            List<String> preview = toList(raw.get(rawKey), c);
            if (!preview.isEmpty()) {
                ret.put(yamlKey, preview);
            }
        };
        ret.put("install", "true".equals(raw.get("doInstall")));
        doPutArgs.accept("buildtoolArgs", "catkin_make_args", ' ');
        doPutArgs.accept("cmakeArgs", "cmake_args", ' ');
        doPutArgs.accept("makeArgs", "make_args", ' ');
        ret.put("build_space", toRelativePath(raw.get("buildDir")));
        ret.put("devel_space", toRelativePath(raw.get("develDir")));
        ret.put("install_space", toRelativePath(raw.get("installDir")));
        ret.put("source_space", toRelativePath(raw.get("sourceDir")));
        doPutArgs.accept("allowList", "whitelist", ',');
        doPutArgs.accept("denyList", "blacklist", ',');
        return ret;
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
            try {
                Map<String, Object> data = YAML.load(colconDefaults.getInputStream());
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
            VirtualFile configFile = Optional.ofNullable(profileDir.findChild("config.yaml"))
                    .orElseGet(() -> profileDir.findChild("build.yaml"));
            if (configFile == null) {
                continue;
            }
            try {
                Map<String, Object> data = YAML.load(configFile.getInputStream());
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

    @NotNull
    private List<String> toList(String data, char delimiter) {
        List<String> ret = delimiter == ':' ? PathListUtil.parsePathList(data) : Arrays.asList(data.split("" + delimiter));
        if (ret.size() == 1 && ret.get(0).isEmpty()) {
            return Collections.emptyList();
        }
        return ret;
    }

    private String extendPath(@NotNull Map<String, Object> data, String lookupKey, String def) {
        String raw = (String) data.getOrDefault(lookupKey, def);
        return raw.startsWith("/") ? raw : settings.getWorkspacePath() + "/" + raw;
    }

    private String extendPath(@NotNull String raw) {
        return raw.startsWith("/") ? raw : settings.getWorkspacePath() + "/" + raw;
    }

    @NotNull
    private String toRelativePath(@NotNull String fullPath) {
        if (fullPath.startsWith(settings.getWorkspacePath() + "/")) {
            return fullPath.substring(settings.getWorkspacePath().length() + 1);
        }
        return fullPath;
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
