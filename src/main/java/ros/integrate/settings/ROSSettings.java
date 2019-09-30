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

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.LinkedList;
import java.util.Queue;

@State(name = "ROSSettings", storages = {@Storage("ros.cfg")})
public class ROSSettings implements PersistentStateComponent<ROSSettings> {

    private Project project;
    private String rosPath = System.getenv("ROS_ROOT");

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

    public void setRosPath(String rosPath) {
        this.rosPath = rosPath;
    }
}
