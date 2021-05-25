package ros.integrate.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Pair;
import com.intellij.ui.RecentsManager;
import com.intellij.ui.TextFieldWithHistory;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class PathTextFieldWithHistory extends TextFieldWithHistory {

    protected final TextFieldWithBrowseButton field = new TextFieldWithBrowseButton() {
        @Override
        protected @NotNull Icon getDefaultIcon() {
            return getBuiltinIcons().first;
        }

        @Override
        protected @NotNull Icon getHoveredIcon() {
            return getBuiltinIcons().second;
        }
    };
    protected String historyIndex = "";

    public PathTextFieldWithHistory() {
        setEditable(true);
        setEditor(new BasicComboBoxEditor() {
            @Override
            protected JTextField createEditorComponent() {
                return field.getTextField();
            }
        });
    }

    /**
     * attaches functionality to the ... button as well as a history lookup when filling in the text in the text field
     * @param options an options object loading a bunch of useful settings like window title,
     *                history storage, etc.
     */
    public void installFeatures(@NotNull BrowserOptions options) {

        List<String> recentEntries = Optional.ofNullable(RecentsManager.getInstance(options.project)
                .getRecentEntries(options.getKey()))
                .orElse(new LinkedList<>());
        recentEntries.remove(getText()); // doing this and the line below will move curDir to the top regardless if it exists or not
        recentEntries.add(0, getText());
        setHistory(recentEntries);
        historyIndex = options.getKey();

        installBrowseButton(options);
    }

    protected void installBrowseButton(@NotNull BrowserOptions options) {
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        field.addBrowseFolderListener(options.title, options.description, options.project, descriptor);
    }


    public void addToHistory(Project project, @NotNull Consumer<String> updateAction) {
        RecentsManager.getInstance(project).registerRecentEntry(historyIndex, field.getText());
        updateAction.consume(field.getText());
    }

    protected Pair<Icon, Icon> getBuiltinIcons() {
        return new Pair<>(AllIcons.General.OpenDisk, AllIcons.General.OpenDiskHover);
    }
}
