package ros.integrate.settings;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.containers.MultiMap;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.ui.PathListUtil;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * stores user configurations that persist after the IDE is closed. These settings are per project, however.
 * @author Noam Dori
 */
@State(name = "ROSSettings",storages = @Storage("ros.xml"))
public class ROSSettings implements PersistentStateComponent<ROSSettings.State> {
    private static final Logger LOG = Logger.getLogger("#ros.integrate.settings.ROSSettings");
    private static boolean settingsLoaded = false;
    private static final Properties prop = loadProperties();
    private final Project project;

    @NotNull
    private static Properties loadProperties() {
        Properties ret = new Properties();
        try {
            ret.load(ROSSettings.class.getClassLoader().getResourceAsStream("defaults.properties"));
            settingsLoaded = true;
        } catch (IOException e) {
            LOG.warning("could not load configuration file, default values will not be loaded. error: " +
                    e.getMessage());
        }
        return ret;
    }

    /**
     * the actual data of the settings
     */
    @SuppressWarnings("WeakerAccess")
    static class State {
        public String rosPath;
        public String workspacePath;
        public String additionalSources;
        public String excludedXmls;
        public String licenseLinkType;
        public String knownKeys;
        public String depSources;
    }
    private State state = new State();
    private final MultiMap<String,Consumer<ROSSettings>> listeners = new MultiMap<>();

    /**
     * construct a new settings service
     * @param project the project this configuration entity belongs to
     */
    @Contract(pure = true)
    private ROSSettings(Project project) {
        String rosPath = System.getenv("ROS_ROOT");
        if(rosPath == null) {
            state.rosPath = "";
        } else {
            state.rosPath = rosPath.substring(0, rosPath.length() - "/share/ros".length());
        }

        state.workspacePath = "";

        // use System.getenv("ROS_PACKAGE_PATH") (with filter) once we no longer need to init from .bashrc
        state.additionalSources = "";

        state.excludedXmls = "";

        state.licenseLinkType = "";

        state.knownKeys = "";

        state.depSources = "";

        this.project = project;

        if (settingsLoaded) {
            state.depSources = prop.getProperty("depSources"); // " is the standard delimiter for URLs
            state.knownKeys = prop.getProperty("knownKeys");
            state.licenseLinkType = prop.getProperty("licenseLinkType");
        }
    }

    @Override
    public void initializeComponent() {
        if (getWorkspacePath().isEmpty()) {
            Optional.ofNullable(ROSSettingsUtil.detectWorkspace(project)).ifPresent(this::setWorkspacePath);
        }
    }

    /**
     * a shortcut to get the ROS settings entity
     * @param project the project from which to get the settings page
     * @return the instance of the settings of this project
     */
    public static ROSSettings getInstance(@NotNull Project project) {
        return project.getService(ROSSettings.class);
    }

    @Nullable
    @Override
    public State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    /**
     * triggers all stored subscribers that are listening to a specific topic
     * @param topic the topic on which all listeners are triggered
     */
    void triggerListeners(String topic) {
        listeners.get(topic).forEach(listener -> listener.accept(this));
    }

    /**
     * adds a subscriber to a specific topic. Once this topic is triggered, the function runs.
     * @param trigger the function to run when the topic is triggered
     * @param topic the topic to link the subscriber to
     */
    public void addListener(Consumer<ROSSettings> trigger, String topic) {
        listeners.putValue(topic, trigger);
    }

    /**
     * adds a subscriber to multiple topics. Once one of those topics is triggered, the function runs.
     * @param trigger the function to run when a topic is triggered
     * @param topics a list of all topics to link the subscriber to
     */
    public void addListener(Consumer<ROSSettings> trigger, @NotNull String[] topics) {
        for (String topic : topics) {
            listeners.putValue(topic, trigger);
        }
    }

    /**
     * @return the path to the ROS installation directory. There all ROS files are placed
     * (for example, setup.sh is directly in it)
     */
    public String getROSPath() {
        return state.rosPath;
    }

    /**
     * sets the path to the ROS installation directory
     * @param rosPath the new path
     */
    void setRosPath(String rosPath) {
        state.rosPath = rosPath;
    }

    /**
     * @return the path to the workspace directory. In ROS1 this was commonly called catkin_ws.
     * In ROS2 it is commonly called dev_ws. In here, all the source files the user works on are placed,
     * and whatever is generated from them
     */
    public String getWorkspacePath() {
        return state.workspacePath;
    }

    /**
     * sets the path to the workspace directory.
     * @param workspacePath the new path
     */
    void setWorkspacePath(String workspacePath) {
        state.workspacePath = workspacePath;
    }

    /**
     * gets all additional sources that are not in the project workspace.
     * @return a copy of the list of additional sources available. This list may be modified.
     */
    public List<String> getAdditionalSources() {
        return PathListUtil.parsePathList(state.additionalSources);
    }

    /**
     * @return the serialized raw data of the additional sources
     */
    String getRawAdditionalSources() {
        return state.additionalSources;
    }

    /**
     * sets the additional paths to source packages
     * @param rawAdditionalSources the new list of paths
     */
    void setAdditionalSources(String rawAdditionalSources) {
        state.additionalSources = rawAdditionalSources;
    }

    /**
     * gets all XML file paths that are excluded from indexing by the ROS plugin.
     * @return a copy of the list of excluded XMLs. This list may be modified.
     */
    public List<String> getExcludedXmls() {
        return PathListUtil.parsePathList(state.excludedXmls);
    }

    /**
     * removes an XML file from indexing in the ROS plugin.
     * @param xmlPath the path to the XML file to ignore
     */
    public void addExcludedXml(String xmlPath) {
        List<String> parsed = PathListUtil.parsePathList(state.excludedXmls);
        if (!parsed.contains(xmlPath)) {
            parsed.add(xmlPath);
        }
        state.excludedXmls = PathListUtil.serializePathList(parsed);
    }

    /**
     * adds an XML file for indexing in the ROS plugin.
     * @param xmlPath the path to the XML file to recognize
     */
    public void removeExcludedXml(String xmlPath) {
        List<String> parsed = PathListUtil.parsePathList(state.excludedXmls);
        parsed.remove(xmlPath);
        state.excludedXmls = PathListUtil.serializePathList(parsed);
    }

    /**
     * @return the serialized raw data of the excluded XML paths
     */
    String getRawExcludedXmls() {
        return state.excludedXmls;
    }

    /**
     * sets, not just add/removes the list of excluded XML paths from indexing
     * @param excludedXmls a list of XML file paths to ignore when indexing
     */
    void setExcludedXmls(String excludedXmls) {
        state.excludedXmls = excludedXmls;
    }

    /**
     * @return a list of rosdep keys to be remembered by the IDE without the need to go online.
     * These can then be depended on.
     */
    public List<String> getKnownROSDepKeys() {
        return Arrays.asList(state.knownKeys.split(":"));
    }

    /**
     * @return the serialized raw data of the known rosdep keys
     */
    String getRawKnownROSDepKeys() {
        return state.knownKeys;
    }

    /**
     * sets, not just add/remove the list of known rosdep keys
     * @param knownKeys the list of rosdep keys to remember
     */
    void setKnownROSDepKeys(String knownKeys) {
        state.knownKeys = knownKeys;
    }

    /**
     * adds a new rosdep key to remember when looking for dependencies
     * @param name the new rosdep
     */
    public void addKnownROSDepKey(@NotNull String name) {
        if (!name.isEmpty() && !state.knownKeys.matches("^(.*:)?" + name + "(:.*)?$")) {
            state.knownKeys = state.knownKeys.concat((state.knownKeys.isEmpty() ? "" : ":") + name);
        }
    }

    /**
     * @return a list of online URLs that contain all dependencies. When necessary, the plugin will download these pages
     * and extract the dependencies from them
     */
    public List<String> getROSDepSources() {
        return Arrays.asList(state.depSources.split("\""));
    }

    /**
     * @return the serialized raw data of the rosdep source URLs
     */
    String getRawROSDepSources() {
        return state.depSources;
    }

    /**
     * sets the list of online URLs from which to extract possible dependencies
     * @param sources a URL list (delimited by <code>"</code>) of the lists of rosdep keys
     */
    void setROSDepSources(String sources) {
        state.depSources = sources;
    }

    /**
     * @return the type of pages to show as documentation for a license
     */
    public String getLicenseLinkType() {
        return state.licenseLinkType;
    }

    /**
     * sets the type of pages to show as documentation for a license
     * @param licenseLinkType the name of the type of pages.
     *                        See {@link ros.integrate.pkg.xml.ROSLicenses.LicenseEntity#getLink(String)} for details
     */
    public void setLicenseLinkType(String licenseLinkType) {
        state.licenseLinkType = licenseLinkType;
    }
}
