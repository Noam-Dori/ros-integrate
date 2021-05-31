package ros.integrate.ui;

import org.jetbrains.annotations.Contract;

/**
 * describes different keys to look for past user choices in the history cache
 */
public enum HistoryKey {
    ROS_ROOT("ROSSettings","RECENT_KEYS"),
    WORKSPACE("ROSSettings","WORKSPACE"),
    EXTRA_SOURCES("ROSSettings","EXTRA_SOURCES"),
    EXCLUDED_XMLS("ROSSettings","EXCLUDED_XMLS"),
    ROSDEP_SOURCES("ROSSettings","ROSDEP_SOURCES"),
    KNOWN_ROSDEP_KEYS("ROSSettings","KNOWN_ROSDEP_KEYS"),
    LICENSE_LINK_TYPE("ROSSettings","LICENSE_LINK_TYPE"),
    PROFILE_SOURCE("ROSProfile","SOURCE_DIR"),
    PROFILE_BUILD("ROSProfile","BUILD_DIR"),
    PROFILE_DEVEL("ROSProfile","DEVEL_DIR"),
    PROFILE_INSTALL("ROSProfile","INSTALL_DIR"),
    PACKAGE_LISTS("ROSProfile", "ALLOWED_PACKAGES");

    private final String historyKey;

    /**
     * construct a new history key
     * @param lookupKey the name of the key.
     */
    @Contract(pure = true)
    HistoryKey(String category, String lookupKey) {
        this.historyKey = category + "." + lookupKey;
    }

    /**
     * @return the immutable key to look for history of that specific type
     */
    @Contract(pure = true)
    public String get() {
        return historyKey;
    }
}
