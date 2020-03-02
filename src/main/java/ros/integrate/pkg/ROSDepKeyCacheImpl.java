package ros.integrate.pkg;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.psi.impl.ROSDepKey;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

public class ROSDepKeyCacheImpl implements ROSDepKeyCache {
    private static final Logger LOG = Logger.getLogger("#ros.integrate.pkg.ROSDepKeyCacheImpl");

    private final ConcurrentMap<String, ROSDepKey> keyCache = new ConcurrentHashMap<>();
    private final Project project;
    private boolean internetAttempted = false;

    public ROSDepKeyCacheImpl(Project project) {
        this.project = project;
    }

    @Override
    public void projectOpened() {
        init();
    }

    @Override
    public void initComponent() {
        if (project.isInitialized()) {
            init();
        }
    }

    private void init() {
        // TODO load offline stuff from ROSSettings.
    }

    @Nullable
    @Override
    public ROSDepKey findKey(@NotNull String name) {
        ROSDepKey ret = keyCache.get(name);
        if (ret != null) {
            return ret;
        }
        tryCachingKeys();
        ret = keyCache.get(name);
        if (ret != null) {
            saveKey(ret);
        }
        return ret;
    }

    private void saveKey(ROSDepKey ret) {
        // TODO will be implemented by saving to ROSSettings.
    }

    @NotNull
    @Override
    public Collection<ROSDepKey> getAllKeys() {
        tryCachingKeys();
        return keyCache.values();
    }

    private synchronized void tryCachingKeys() {
        if (internetAttempted) {
            return;
        }
        try {
//            boolean allConnected = true;
            for (String address : getSources()) {
                try {
                    Scanner scanner = new Scanner(new URL(address).openStream());
                    while (scanner.hasNextLine()) {
                        String keyName = nextKey(scanner);
                        keyCache.putIfAbsent(keyName, new ROSDepKey(project, keyName));
                    }
                } catch (IOException e) {
                    LOG.warning("Could not fetch rosdep source list: no connection to " + address);
//                    allConnected = false;
                }
            }
//            onlineMode = allConnected; TODO used for annotation and allowing the user to force caching.
            internetAttempted = true;
        } catch (IOException e) {
            LOG.severe("could not load configuration file, error: " + e.getMessage());
        }
    }

    @NotNull
    private String[] getSources() throws IOException {
        // TODO use ROSSettings here to get sources instead of a .properties file.
        Properties ret = new Properties();
        ret.load(this.getClass().getClassLoader().getResourceAsStream("rosdep.properties"));
        return ret.getProperty("values").split("\""); // " is the standard delimiter for URLs
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
}
