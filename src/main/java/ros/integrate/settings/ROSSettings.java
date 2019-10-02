package ros.integrate.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

@State(name = "ROSSettings", storages = {@Storage("ros.cfg")})
public class ROSSettings implements PersistentStateComponent<ROSSettings> {

    private Project project;
    private String rosPath = System.getenv("ROS_ROOT");
    private List<Consumer<ROSSettings>> listeners = new LinkedList<>();

    @Contract(pure = true)
    public ROSSettings(Project project) {
        this.project = project;
    }

    public static ROSSettings getInstance(Project project) {
        return ServiceManager.getService(project, ROSSettings.class);
    }

    @Nullable
    @Override
    public ROSSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ROSSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public String getROSPath() {
        return rosPath;
    }

    void setRosPath(String rosPath) {
        this.rosPath = rosPath;
    }

    void triggerListeners() {
        listeners.forEach(listener -> listener.accept(this));
    }

    public void addListener(Consumer<ROSSettings> trigger) {
        listeners.add(trigger);
    }
}
