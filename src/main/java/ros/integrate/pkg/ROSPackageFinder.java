package ros.integrate.pkg;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.util.containers.MultiMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.psi.ROSPackage;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * an extension point used to find and add ROS packages to the package index,
 * as well as load/create new relevant libraries
 * @author Noam Dori
 */
public interface ROSPackageFinder {
    ExtensionPointName<ROSPackageFinder> EP_NAME = ExtensionPointName.create("ros-integrate.packageFinder");

    List<ROSPackageFinder> FINDERS = ROSPackageFinder.EP_NAME.getExtensionList();

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


    /**
     * investigates a specific xml to check if it creates a package as a result, then stores the result.
     * @param vXml the virtual XML file to check.
     * @param project the project this file belongs to
     * @param pkgCache the cache to store the new package in.
     * @implNote any package you find and create needs to be added to the cache.
     *           Make sure to not stumble into package duplicates.
     *           packages are mapped by their package.xml name
     *           which should be the same as the directory name and the CMake project name.
     */
    @SuppressWarnings({"unused", "RedundantSuppression"})
    void findAndCacheOneFile(@NotNull VirtualFile vXml, Project project, ConcurrentMap<String, ROSPackage> pkgCache);

    /**
     * investigate the events provided to see if they mean a new package was created.
     *
     * @param project the project this finder will investigate
     * @param events the file events the finders checks
     * @return <code>Pair.create("",{@link ROSPackage#ORPHAN})</></code> if there is no need to create a package,
     *         otherwise, a pair from the package's name, to the new package the finder created.
     */
    MultiMap<ROSPackage, VFileEvent> investigate(Project project, Collection<VFileEvent> events);

    /**
     *
     *
     * @param project the project the finder uses as reference
     * @param pkg the package to investigate
     * @return what should happen to the cache:
     *         {@link CacheCommand#NONE} if the cache should do nothing
     *         {@link CacheCommand#RENAME} if the cache should rename the package.
     *         {@link CacheCommand#DELETE} if the cache should delete the package.
     */
    @Nullable
    CacheCommand investigateChanges(Project project, ROSPackage pkg);

    /**
     * loads the artifacts this finder uses
     * @param project the project the finder will search/create an artifact for
     */
    void loadLibraries(Project project);

    /**
     * checks and updates the libraries this finder loaded.
     * @param project the project the finder will update a library for
     * @return true if the libraries were, false otherwise.
     */
    boolean updateLibraries(Project project);

    /**
     * loads the dependencies of the main module on the artifacts this finder loaded.
     * @param module the original module used to develop code
     */
    void setDependency(Module module);

    /**
     * represents possible commands that can be applied in certain items in the cache:
     * either rename the item, delete the item, or do nothing
     */
    enum CacheCommand {
        NONE,
        RENAME,
        DELETE
    }
}
