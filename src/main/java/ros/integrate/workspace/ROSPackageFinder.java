package ros.integrate.workspace;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.util.containers.MultiMap;
import org.jetbrains.annotations.Nullable;
import ros.integrate.workspace.psi.ROSPackage;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

/**
 * an extension point used to find and add ROS packages to the cache
 */
public interface ROSPackageFinder {
    ExtensionPointName<ROSPackageFinder> EP_NAME = ExtensionPointName.create("ros-integrate.packageFinder");

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
     * fetches the library this finder uses
     * @param project the project the finder will search/create a library for
     * @return {@code null} if the finder does not rely on a library,
     *         otherwise the library this finder uses for its packages.
     */
    @Nullable
    Library getLibrary(Project project);

    enum CacheCommand {
        NONE,
        RENAME,
        DELETE
    }
}
