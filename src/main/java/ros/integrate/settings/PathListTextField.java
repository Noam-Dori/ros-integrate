package ros.integrate.settings;

import com.intellij.refactoring.copy.CopyFilesOrDirectoriesDialog;
import com.intellij.ui.RecentsManager;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

class PathListTextField extends TextFieldWithHistoryWithBrowseButton {
    void installHistoryAndDialog(@NotNull RecentsManager recentsManager, @NotNull BrowserOptions options) {
        List<String> recentEntries = Optional.ofNullable(recentsManager.getRecentEntries(options.getKey()))
                .orElse(new LinkedList<>());
        recentEntries.remove(getText()); // doing this and the line below will move curDir to the top regardless if it exists or not
        recentEntries.add(0,getText());
        getChildComponent().setHistory(recentEntries);

        setTextFieldPreferredWidth(CopyFilesOrDirectoriesDialog.MAX_PATH_LENGTH);
    }
}
