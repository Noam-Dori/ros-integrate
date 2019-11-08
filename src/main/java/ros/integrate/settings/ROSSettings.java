package ros.integrate.settings;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.containers.MultiMap;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

@State(name = "ROSSettings",storages = @Storage("ros.xml"))
public class ROSSettings implements PersistentStateComponent<ROSSettings.State> {
    @SuppressWarnings("WeakerAccess")
    static class State {
        public String rosPath;
        public String workspacePath;
        public String additionalSources;
        public String excludedXmls;
    }
    private final State state = new State();
    private final MultiMap<String,Consumer<ROSSettings>> listeners = new MultiMap<>();

    @Contract(pure = true)
    private ROSSettings(Project project) {
        String rosPath = System.getenv("ROS_ROOT");
        if(rosPath == null) {
            state.rosPath = "";
        } else {
            state.rosPath = rosPath.substring(0, rosPath.length() - "/share/ros".length());
        }

        String workspacePath = ROSSettingsUtil.detectWorkspace(project);
        state.workspacePath = workspacePath == null ? "" : workspacePath;

        String additionalSources = System.getenv("ROS_PACKAGE_PATH");
        state.additionalSources = additionalSources == null ? "" : additionalSources;

        state.excludedXmls = "";
    }

    public static ROSSettings getInstance(Project project) {
        return ServiceManager.getService(project, ROSSettings.class);
    }

    @Nullable
    @Override
    public State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        XmlSerializerUtil.getAccessors(State.class).forEach(accessor ->
                Optional.ofNullable(accessor.read(state))
                        .ifPresent(val -> accessor.set(this.state, val)));
    }

    void triggerListeners(String topic) {
        listeners.get(topic).forEach(listener -> listener.accept(this));
    }

    public void addListener(Consumer<ROSSettings> trigger, String topic) {
        listeners.putValue(topic, trigger);
    }

    public void addListener(Consumer<ROSSettings> trigger, @NotNull String[] topics) {
        for (String topic : topics) {
            listeners.putValue(topic, trigger);
        }
    }

    public String getROSPath() {
        return state.rosPath;
    }

    void setRosPath(String rosPath) {
        state.rosPath = rosPath;
    }

    public String getWorkspacePath() {
        return state.workspacePath;
    }

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

    String getRawAdditionalSources() {
        return state.additionalSources;
    }

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

    public void addExcludedXml(String xmlPath) {
        List<String> parsed = PathListUtil.parsePathList(state.excludedXmls);
        if (!parsed.contains(xmlPath)) {
            parsed.add(xmlPath);
        }
        state.excludedXmls = PathListUtil.serializePathList(parsed);
    }

    public void removeExcludedXml(String xmlPath) {
        List<String> parsed = PathListUtil.parsePathList(state.excludedXmls);
        parsed.remove(xmlPath);
        state.excludedXmls = PathListUtil.serializePathList(parsed);
    }

    String getRawExcludedXmls() {
        return state.excludedXmls;
    }

    void setExcludedXmls(String excludedXmls) {
        state.excludedXmls = excludedXmls;
    }
}
