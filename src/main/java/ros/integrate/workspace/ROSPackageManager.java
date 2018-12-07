package ros.integrate.workspace;

import org.jetbrains.annotations.Nullable;
import ros.integrate.workspace.psi.ROSPackage;

import java.util.List;

public interface ROSPackageManager {
    /**
     * retrieves all packages in the cache (and tries to find more)
     * @return all packages the project knows about.
     */
    List<ROSPackage> getAllPackages();

    /**
     * finds a ROS package with the provided name.
     * @param pkgName the name of the package (which is also its fully qualified name)
     * @return null if a ROS package with the given name was not found, otherwise,
     */
    @Nullable
    ROSPackage findPackage(String pkgName);
}
