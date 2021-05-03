package ros.integrate.buildtool;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

/**
 * stores information about ROS build profiles which persists between IDE sessions.
 * However, this will only store information that does not exist in any ROS configuration file.
 * @author Noam Dori
 */
// TODO: 5/4/2021 implement away the mock stuff
public class ROSProfiles {
    /**
     * a shortcut to get the ROS settings entity
     * @param project the project from which to get the settings page
     * @return the instance of the settings of this project
     */
    public static ROSProfiles getInstance(@NotNull Project project) {
        return project.getService(ROSProfiles.class);
    }

    @Nullable
    public <T> T getProfileProperty(int id, Function<ROSProfile, T> method) {
        return Optional.ofNullable(getProfile(id)).map(method).orElse(null);
    }

    private ROSProfile getProfile(int id) {
        return mockProfile;
    }

    public Integer requestId() {
        return 1;
    }

    private final ROSProfile mockProfile = new ROSProfile();
}
