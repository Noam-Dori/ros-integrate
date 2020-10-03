package ros.integrate.pkg;

import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.psi.ROSPackage;

import java.util.Collection;
import java.util.List;

/**
 * the complete index of all ROS packages from here you can get all available packages, find specific packages,
 * and edit the index (or some indexing settings).
 * As a bonus, this index also handles group management (from package.xml group tags)
 * @author Noam Dori
 */
public interface ROSPackageManager {
    /**
     * retrieves all packages in the cache (and tries to find more)
     * @return all packages the project knows about.
     * @apiNote note that is is only a VIEW into the available packages and may only be read.
     * If you want to run changes in a calculation, you must copy this collection.
     * @implNote Only finders can modify the contents of the package manager.
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

    /**
     * removes an XML file from indexing in the ROS plugin.
     * @param file the file to ignore
     */
    void excludePkgXml(XmlFile file);

    /**
     * adds an XML file for indexing in the ROS plugin.
     * @param file the file to recognize
     */
    void includeXml(XmlFile file);

    /**
     * searches for all members of a specific group within the workspace.
     * @param groupName the group to look for
     * @return a collection of all the members of the group. If there are none, an empty collection is returned.
     * @apiNote this action can be heavy to calculate since it runs text operations on all package.xml files.
     *          Use it sparingly.
     */
    @NotNull
    Collection<ROSPackage> findGroupMembers(@NotNull String groupName);

    /**
     * searches for all packages within the workspace that depend on a specific group.
     * @param groupName the group to look for
     * @return a collection of all the members of the group. If there are none, an empty collection is returned.
     * @apiNote this action can be heavy to calculate since it runs text operations on all package.xml files.
     *          Use it sparingly.
     */
    @SuppressWarnings({"unused", "RedundantSuppression"})
    @NotNull
    Collection<ROSPackage> findGroupDependents(@NotNull String groupName);

    /**
     * reloads the package manager index.
     */
    void reloadIndex();

    /**
     * indicates that relevant files changed, so the index should try updating
     * @param events the events that happened
     */
    void filesChanged(List<? extends VFileEvent> events);
}
