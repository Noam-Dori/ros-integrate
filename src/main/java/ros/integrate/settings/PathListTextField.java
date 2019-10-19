package ros.integrate.settings;

import com.intellij.execution.util.PathMappingsComponent; // for reference
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.refactoring.copy.CopyFilesOrDirectoriesDialog;
import com.intellij.ui.RecentsManager;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class PathListTextField extends TextFieldWithHistoryWithBrowseButton {
    private static class PackagePathDialog extends DialogWrapper {
        private final JPanel display = new JPanel(new BorderLayout());
        private final PathListTextField parent;

        private final PackagePathTable data;

        PackagePathDialog(PathListTextField component) {
            super(component, true);
            parent = component;
            data = new PackagePathTable();

            setTitle("Configure Paths to Source");
            data.setValues(Arrays.stream(component.getText()
                    .split(":"))
                    .filter(path -> path.equals(""))
                    .collect(Collectors.toList()));
            display.add(data.getComponent(),BorderLayout.CENTER);
            init();
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            return display;
        }

        @Override
        protected void doOKAction() {
            data.stopEditing();

            parent.setPaths(data.getPaths());

            super.doOKAction();
        }
    }

    void installHistoryAndDialog(@NotNull RecentsManager recentsManager, @NotNull BrowserOptions options) {
        addActionListener(actionEvent -> new PackagePathDialog(this).show());

        List<String> recentEntries = Optional.ofNullable(recentsManager.getRecentEntries(options.getKey()))
                .orElse(new LinkedList<>());
        recentEntries.remove(getText()); // doing this and the line below will move curDir to the top regardless if it exists or not
        recentEntries.add(0,getText());
        getChildComponent().setHistory(recentEntries);

        setTextFieldPreferredWidth(CopyFilesOrDirectoriesDialog.MAX_PATH_LENGTH);
    }

    private void setPaths(@NotNull List<String> paths) {
        setText(paths.stream().filter(path -> !path.isEmpty()).collect(Collectors.joining(":")));
    }
}
