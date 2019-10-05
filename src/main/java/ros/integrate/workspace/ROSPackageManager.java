package ros.integrate.workspace;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.Nullable;
import ros.integrate.workspace.psi.ROSPackage;

import java.util.Collection;

public interface ROSPackageManager extends ProjectComponent {
    /**
     * retrieves all packages in the cache (and tries to find more)
     * @return all packages the project knows about.
     */
    Collection<ROSPackage> getAllPackages();

    /**
     * finds a ROS package with the provided name.
     * @param pkgName the name of the package (which is also its fully qualified name)
     * @return null if a ROS package with the given name was not found,
     * otherwise, the package stored in the cache with that name.
     */
    @Nullable
    ROSPackage findPackage(String pkgName);

    /**
     * finds a ROS package that contains the provided directory
     * @param childDirectory the directory to use as a test whether or not the package is the right one.
     * @return null is no package was found, otherwise, the package stored in the cache with that directory.
     */
    @Nullable
    ROSPackage findPackage(PsiDirectory childDirectory);

    /**
     * updates the package's information in the cache.
     * @param pkg the package to update
     * @param newName the new name of the package.
     */
    void updatePackageName(ROSPackage pkg, String newName);
}
