package ros.integrate.workspace;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import ros.integrate.workspace.psi.ROSPackage;

import java.util.concurrent.ConcurrentMap;

/**
 * an extension point used to find and add ROS packages to the cache
 */
public interface ROSPackageFinder {
    ExtensionPointName<ROSPackageFinder> EP_NAME = ExtensionPointName.create("ros.integrate.workspace.ROSPackageFinder");

    /**
     * the main action which finds ROS packages based on their key-files,
     * searches for them in a provided cache, and if they don't exist,
     * creates them and adds them to the cache
     * @param project the project this finder belongs to
     * @param pkgCache the current package cache.
     * @implNote any package you find and create needs to be added to the cache.
     *           Make sure to not stumble into package duplicates.
     *           packages are mapped by their package.xml name
     *           which should be the same as the directory name and the CMake project name.
     */
    void findAndCache(Project project, ConcurrentMap<String, ROSPackage> pkgCache);
}
