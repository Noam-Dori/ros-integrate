package ros.integrate.buildtool;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

/**
 * a non-persistent database layer that loads, indexes, and stores buildtool profiles.
 * This is a project service, so changing projects or closing the IDE will clear the database.
 * The persistent information is stored in the buildtool files if possible,
 * and the IDE will hold information missing from those files in a separate, persistent database.
 * @author Noam Dori
 */
// TODO: 5/4/2021 implement away the mock stuff
public class ROSProfiles {
    /**
     * a shortcut to get the ROS profiles database
     * @param project the project from which to get the profiles DB
     * @return the instance of the settings of this project
     */
    public static ROSProfiles getInstance(@NotNull Project project) {
        return project.getService(ROSProfiles.class);
    }

    @Nullable
    public <T> T getProfileProperty(int id, Function<ROSProfile, T> method) {
        return Optional.ofNullable(getProfile(id)).map(method).orElse(null);
    }

    public ROSProfile getProfile(int id) {
        return mockProfile;
    }

    public Integer requestId() {
        return 1;
    }

    private final ROSProfile mockProfile = new ROSProfile();
}
