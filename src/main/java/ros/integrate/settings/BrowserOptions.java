package ros.integrate.settings;

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
        DEFAULT("RECENT_KEYS"),
        WORKSPACE("WORKSPACE"),
        EXTRA_SOURCES("EXTRA_SOURCES"),
        EXCLUDED_XMLS("EXCLUDED_XMLS"),
        ROSDEP_SOURCES("ROSDEP_SOURCES"),
        KNOWN_ROSDEP_KEYS("KNOWN_ROSDEP_KEYS"),
        LICENSE_LINK_TYPE("LICENSE_LINK_TYPE");

        private final String historyKey;

        /**
         * construct a new history key
         * @param lookupKey the name of the key.
         */
        @Contract(pure = true)
        HistoryKey(String lookupKey) {
            this.historyKey = "ROSSettings." + lookupKey;
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

    String title = "", description = "", dialogTitle = "";
    char delimiter = ':';
    boolean addBrowser = true;
    private HistoryKey key = HistoryKey.DEFAULT;

    /**
     * construct a new options object with the default history key
     * @param project the project this options belongs to
     */
    @Contract(pure = true)
    BrowserOptions(Project project) {
        this.project = project;
    }

    /**
     * construct a new options object with a custom history key
     * @param project the project this options belongs to
     */
    @Contract(pure = true)
    BrowserOptions(Project project, HistoryKey historyKey) {
        this.project = project;
        this.key = historyKey;
    }

    /**
     * adds a custom description, used as the subtitle in some dialog windows
     * @param description the new string to use
     * @return this with the modifications applied
     */
    BrowserOptions withDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * adds a custom title, used as the title in 2nd level dialog windows
     * @param title the new string to use
     * @return this with the modifications applied
     */
    BrowserOptions withTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * adds a custom title, used as the title in 1st level dialog windows
     * @param dialogTitle the new string to use
     * @return this with the modifications applied
     */
    BrowserOptions withDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
        return this;
    }

    /**
     * for PathListTables, this allows using a custom delimiter between the paths when compiling them to a single string.
     * @param delimiter the new delimiter
     * @return this with the modifications applied
     */
    BrowserOptions withDelimiter(@SuppressWarnings("SameParameterValue") char delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    /**
     * removes the "browse for file" option from the dialog
     * @return this with the modifications applied
     */
    BrowserOptions noFilePaths() {
        this.addBrowser = false;
        return this;
    }

    /**
     * @return the lookup key to use for history lookup.
     */
    String getKey() {
        return key.get();
    }
}
