package ros.integrate.pkg;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.psi.impl.ROSDepKey;
import ros.integrate.ui.BrowserOptions;
import ros.integrate.settings.ROSSettings;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * the implementation of the rosdep key cache, based on directly reading the known key lists
 * @author Noam Dori
 */
public class ROSDepKeyCacheImpl implements ROSDepKeyCache {
    private static final Logger LOG = Logger.getLogger("#ros.integrate.pkg.ROSDepKeyCacheImpl");

    private final ConcurrentMap<String, ROSDepKey> keyCache = new ConcurrentHashMap<>();
    private final Project project;
    private ROSSettings settings;
    private boolean internetAttempted = false,
            offlineMode = true;

    /**
     * construct the key cache
     * @param project the project this cache belongs to
     */
    public ROSDepKeyCacheImpl(Project project) {
        this.project = project;
        init();
    }

    private void init() {
        settings = ROSSettings.getInstance(project);
        // we do not delete keys when settings change, no reason to.
        Consumer<ROSSettings> doOfflineCaching = settings -> {
            List<String> offlineKeys = settings.getKnownROSDepKeys();
            offlineKeys.stream().map(key -> new ROSDepKey(project, key))
                .forEach(dep -> keyCache.putIfAbsent(dep.getName(), dep));
            internetAttempted = false;
            offlineMode = true;
        };
        settings.addListener(doOfflineCaching, BrowserOptions.HistoryKey.KNOWN_ROSDEP_KEYS.get());
        doOfflineCaching.accept(settings);
    }

    @Nullable
    @Override
    public ROSDepKey findKey(@NotNull String name) {
        ROSDepKey ret = keyCache.get(name);
        if (ret != null) {
            saveKey(ret);
            return ret;
        }
        tryCachingKeys(false);
        ret = keyCache.get(name);
        if (ret != null) {
            saveKey(ret);
        }
        return ret;
    }

    private void saveKey(@NotNull ROSDepKey ret) {
        settings.addKnownROSDepKey(ret.getName());
    }

    @NotNull
    @Override
    public Collection<ROSDepKey> getAllKeys() {
        tryCachingKeys(false);
        return keyCache.values();
    }

    private void tryCachingKeys(boolean force) {
        if (!force && internetAttempted) {
            return;
        }
        ProgressIndicator indicator = ProgressIndicatorProvider.getGlobalProgressIndicator();
        boolean connectionFailed = false;
        for (String address : getSources()) {
            if (indicator != null) {
                indicator.checkCanceled();
            }
            try {
                URLConnection con = new URL(address).openConnection();
                if (!force) {
                    con.setConnectTimeout(100);
                    con.setReadTimeout(1000);
                }
                Scanner scanner = new Scanner(con.getInputStream());
                while (scanner.hasNextLine()) {
                    String keyName = nextKey(scanner);
                    if (!keyName.isEmpty()) {
                        keyCache.putIfAbsent(keyName, new ROSDepKey(project, keyName));
                    }
                }
            } catch (IOException e) {
                LOG.warning("Could not fetch rosdep source list: failed connection to " + address);
                connectionFailed = true;
            }
        }
        offlineMode = connectionFailed;
        internetAttempted = true;
    }

    @NotNull
    private List<String> getSources() {
        return settings.getROSDepSources();
    }

    @NotNull
    private String nextKey(@NotNull Scanner scanner) {
        String ret = "";
        while (scanner.hasNextLine() && ret.isEmpty()) {
            String line = scanner.nextLine();
            if (!line.contains(" ")) {
                ret = line.substring(0,line.length() - 1);
            }
        }
        return ret;
    }

    @Override
    public boolean inOfflineMode() {
        return offlineMode;
    }

    @Override
    public void forceFetch() {
        tryCachingKeys(true);
    }
}
