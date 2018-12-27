package ros.integrate.workspace;

import com.intellij.openapi.components.ProjectComponent;
import org.jetbrains.annotations.Nullable;
import ros.integrate.workspace.psi.ROSPackage;

import java.util.List;

public interface ROSPackageManager extends ProjectComponent {
    /**
     * retrieves all packages in the cache (and tries to find more)
     * @return all packages the project knows about.
     */
    List<ROSPackage> getAllPackages();

    /**
     * finds a ROS package with the provided name.
     * @param pkgName the name of the package (which is also its fully qualified name)
     * @return null if a ROS package with the given name was not found,
     * otherwise, the package stored in the cache with that name.
     */
    @Nullable
    ROSPackage findPackage(String pkgName);

    /**
     * updates the package's information in the cache.
     * @param pkg the package to update
     * @param newName the new name of the package.
     */
    void updatePackageName(ROSPackage pkg, String newName);
}
