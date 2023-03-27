package ros.integrate.pkg;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.psi.impl.ROSDepKey;

import java.util.Collection;

/**
 * A specialised component that contains information about all rosdep keys available. This component contains an offline
 * cache that is updated based on user input. If a query is run, the following steps are taken:
 * <ol>
 *     <li>The offline cache is first searched (before attempt)</li>
 *     <li>If offline mode fails and no attempt was done, the cache will attempt to fetch the online rosdep lists</li>
 *     <li>If connection is successful:
 *     <ol>
 *         <li>the cache marks that connection was successful</li>
 *         <li>the cache searches for the key in the updated database.</li>
 *         <li>if the key is found, it is saved to the offline cache (a file).</li>
 *     </ol>
 *     </li>
 *     <li>the cache marks that it attempted connection so that it wont attempt it again next time
 *     (even if it failed connecting)</li>
 * </ol>
 * @author Noam Dori
 */
public interface ROSDepKeyCache {

    /**
     * attempts to find a key with the given name
     * @param name the name to search
     * @return {@code null} if no rosdep key was found.
     */
    @Nullable
    ROSDepKey findKey(@NotNull String name);

    /**
     * @return all keys visible to the cache.
     * @apiNote this skips the "before attempt" step mentioned above.
     */
    @NotNull
    Collection<ROSDepKey> getAllKeys();

    /**
     * @return whether all the sources were accessed and processed.
     *         {@code false} if one of the rosdep lists failed access, and {@code true} if all lists in the Settings
     *         were fully processed.
     */
    boolean inOfflineMode();

    /**
     * forces the cache to fetch keys from the sources specified.
     */
    void forceFetch();
}
