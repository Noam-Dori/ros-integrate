package ros.integrate.ui;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Contract;

/**
 * an options object loading a bunch of useful settings like window title, history storage, etc.
 * @author Noam Dori
 */
public class BrowserOptions {
    /**
     * describes different keys to look for past user choices in the history cache
     */
    public enum HistoryKey {
        DEFAULT("ROSSettings","RECENT_KEYS"),
        WORKSPACE("ROSSettings","WORKSPACE"),
        EXTRA_SOURCES("ROSSettings","EXTRA_SOURCES"),
        EXCLUDED_XMLS("ROSSettings","EXCLUDED_XMLS"),
        ROSDEP_SOURCES("ROSSettings","ROSDEP_SOURCES"),
        KNOWN_ROSDEP_KEYS("ROSSettings","KNOWN_ROSDEP_KEYS"),
        LICENSE_LINK_TYPE("ROSSettings","LICENSE_LINK_TYPE"),
        PROFILE_SOURCE("ROSProfile","SOURCE_DIR"),
        PROFILE_BUILD("ROSProfile","BUILD_DIR"),
        PROFILE_DEVEL("ROSProfile","DEVEL_DIR"),
        PROFILE_INSTALL("ROSProfile","INSTALL_DIR");

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

    final Project project;

    public String title = "", description = "", dialogTitle = "";
    char delimiter = ':';
    boolean addBrowser = true;
    private HistoryKey key = HistoryKey.DEFAULT;

    /**
     * construct a new options object with the default history key
     * @param project the project this options belongs to
     */
    @Contract(pure = true)
    public BrowserOptions(Project project) {
        this.project = project;
    }

    /**
     * construct a new options object with a custom history key
     * @param project the project this options belongs to
     */
    @Contract(pure = true)
    public BrowserOptions(Project project, HistoryKey historyKey) {
        this.project = project;
        this.key = historyKey;
    }

    /**
     * adds a custom description, used as the subtitle in some dialog windows
     * @param description the new string to use
     * @return this with the modifications applied
     */
    public BrowserOptions withDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * adds a custom title, used as the title in 2nd level dialog windows
     * @param title the new string to use
     * @return this with the modifications applied
     */
    public BrowserOptions withTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * adds a custom title, used as the title in 1st level dialog windows
     * @param dialogTitle the new string to use
     * @return this with the modifications applied
     */
    public BrowserOptions withDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
        return this;
    }

    /**
     * for PathListTables, this allows using a custom delimiter between the paths when compiling them to a single string.
     * @param delimiter the new delimiter
     * @return this with the modifications applied
     */
    public BrowserOptions withDelimiter(@SuppressWarnings("SameParameterValue") char delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    /**
     * removes the "browse for file" option from the dialog
     * @return this with the modifications applied
     */
    public BrowserOptions noFilePaths() {
        this.addBrowser = false;
        return this;
    }

    /**
     * @return the lookup key to use for history lookup.
     */
    public String getKey() {
        return key.get();
    }
}
