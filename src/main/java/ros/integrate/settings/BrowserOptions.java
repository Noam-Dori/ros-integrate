package ros.integrate.settings;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Contract;

public class BrowserOptions {

    public enum HistoryKey {
        DEFAULT("RECENT_KEYS"),
        WORKSPACE("WORKSPACE"),
        EXTRA_SOURCES("EXTRA_SOURCES"),
        EXCLUDED_XMLS("EXCLUDED_XMLS");

        private final String historyKey;

        @Contract(pure = true)
        HistoryKey(String lookupKey) {
            this.historyKey = "ROSSettings." + lookupKey;
        }

        @Contract(pure = true)
        public String get() {
            return historyKey;
        }
    }

    final Project project;

    String title = "", description = "", dialogTitle = "";
    private HistoryKey key = HistoryKey.DEFAULT;
    @Contract(pure = true)
    BrowserOptions(Project project) {
        this.project = project;
    }

    @Contract(pure = true)
    BrowserOptions(Project project, HistoryKey historyKey) {
        this.project = project;
        this.key = historyKey;
    }

    BrowserOptions withDescription(String description) {
        this.description = description;
        return this;
    }

    BrowserOptions withTitle(String title) {
        this.title = title;
        return this;
    }

    BrowserOptions withDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
        return this;
    }

    String getKey() {
        return key.get();
    }
}
