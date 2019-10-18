package ros.integrate.settings;

import org.jetbrains.annotations.Contract;

class BrowserOptions {
    enum HistoryKey {
        DEFAULT("RECENT_KEYS"),
        WORKSPACE("WORKSPACE"),
        EXTRA_SOURCES("EXTRA_SOURCES");

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

    String title = "", description = "";
    private HistoryKey key = HistoryKey.DEFAULT;
    @Contract(pure = true)
    BrowserOptions() {}

    @Contract(pure = true)
    BrowserOptions(HistoryKey historyKey) {
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

    String getKey() {
        return key.get();
    }
}
