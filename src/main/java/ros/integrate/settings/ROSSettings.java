package ros.integrate.settings;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
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
    }

    @SuppressWarnings({"FieldCanBeLocal", "unused"}) // yes, but its PROJECT level, so we need something to track that. (?)
    private final Project project;
    private final State state = new State();
    private final List<Consumer<ROSSettings>> listeners = new LinkedList<>();

    @Contract(pure = true)
    public ROSSettings(Project project) {
        this.project = project;
        String rosPath = System.getenv("ROS_ROOT");
        state.rosPath = rosPath.substring(0, rosPath.length() - "/share/ros".length());
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
        XmlSerializerUtil.copyBean(state, this.state);
    }

    public String getROSPath() {
        return state.rosPath;
    }

    void setRosPath(String rosPath) {
        state.rosPath = rosPath;
    }

    void triggerListeners() {
        listeners.forEach(listener -> listener.accept(this));
    }

    public void addListener(Consumer<ROSSettings> trigger) {
        listeners.add(trigger);
    }
}
