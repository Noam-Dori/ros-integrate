package ros.integrate.settings;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@State(name = "ROSSettings",storages = @Storage("ros.xml"))
public class ROSSettings implements PersistentStateComponent<ROSSettings.State> {
    @SuppressWarnings("WeakerAccess")
    static class State {
        public String rosPath;
        public String workspacePath;
        public String additionalSources;

        public boolean additionalEnvSync;
    }
    private final State state = new State();
    private final List<Consumer<ROSSettings>> listeners = new LinkedList<>();

    @Contract(pure = true)
    public ROSSettings(Project project) {
        state.additionalEnvSync = true;

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

    void triggerListeners() {
        listeners.forEach(listener -> listener.accept(this));
    }

    public void addListener(Consumer<ROSSettings> trigger) {
        listeners.add(trigger);
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
        return Arrays.stream(state.additionalSources.split(":"))
                .filter(item -> !item.equals(""))
                .collect(Collectors.toList());
    }

    void setAdditionalSources(@NotNull Collection<String> additionalSources) {
        state.additionalSources = String.join(":", additionalSources);
    }

    public boolean isAdditionalEnvSynced() {
        return state.additionalEnvSync;
    }

    void setAdditionalEnvSync(boolean isSynced) {
        state.additionalEnvSync = isSynced;
    }
}
