package ros.integrate.buildtool;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * a non-persistent database layer that loads, indexes, and stores buildtool profiles.
 * This is a project service, so changing projects or closing the IDE will clear the database.
 * The persistent information is stored in the buildtool files if possible,
 * and the IDE will hold information missing from those files in a separate, persistent database.
 * @author Noam Dori
 */
public class ROSProfiles {
    private final Project project;

    /**
     * a shortcut to get the ROS profiles database
     * @param project the project from which to get the profiles DB
     * @return the instance of the settings of this project
     */
    public static ROSProfiles getInstance(@NotNull Project project) {
        return project.getService(ROSProfiles.class);
    }

    private ROSProfiles(Project project) {
        this.project = project;
    }

    /**
     * The database. It is important to note that the identifiers are not persistent in any capacity.
     * Reloading this database will load new identifiers, so no point in storing them.
     * Store the name of the profile instead.
     */
    @NotNull
    private final Map<Integer, ROSProfile> profiles = new HashMap<>();
    private int nextId = 0;

    /**
     * A shortcut operation that gets you a property of the profile with the given ID
     * @param id the ID of the profile in this database.
     * @param method the method applied on the profile to get the property
     * @param <T> the return type of the method
     * @return the result of applying method on the profile with id ID
     */
    @Nullable
    public <T> T getProfileProperty(int id, Function<ROSProfile, T> method) {
        return Optional.ofNullable(getProfile(id)).map(method).orElse(null);
    }

    /**
     * lookup the profile with the corresponding ID
     * @param id the ID to query from the database
     * @return <code>null</code> if there is no profile with the given ID, otherwise, the profile with the ID provided
     */
    @Nullable
    public ROSProfile getProfile(int id) {
        return profiles.get(id);
    }

    /**
     * generate a new profile and request its identifier
     * @return the ID of the newly generated profile
     */
    public Integer requestId() {
        profiles.put(nextId, new ROSProfile());
        return nextId++;
    }

    /**
     * loads all profiles onto this database
     * @return the key list of all profiles loaded in.
     * @apiNote this is a heavy operation as it does a lot of reading. Use sparingly.
     *          this also wipes any existing data.
     */
    public Set<Integer> loadProfiles() {
        profiles.clear();
        nextId = 0;
        ROSProfileLoader profileLoader = new ROSProfileLoader(project);
        for (ROSBuildTool buildTool: ROSBuildTool.values()) {
            profileLoader.load(buildTool).forEach(profile -> {
                profiles.put(nextId, profile);
                nextId++;
            });
        }
        return profiles.keySet();
    }
}
