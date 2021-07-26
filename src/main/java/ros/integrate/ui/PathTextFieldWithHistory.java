package ros.integrate.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Pair;
import com.intellij.ui.RecentsManager;
import com.intellij.ui.TextFieldWithHistory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

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
    protected HistoryKey lookupKey;
    protected Project project;

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
     * attaches functionality to the history lookup when filling in the text in the text field
     * @param project the project this is used to customize
     * @param lookupKey the index to look for previous entries
     */
    public void installHistory(Project project, @NotNull HistoryKey lookupKey) {
        List<String> recentEntries = Optional.ofNullable(RecentsManager.getInstance(project)
                .getRecentEntries(lookupKey.get()))
                .orElse(new LinkedList<>());
        recentEntries.remove(getText()); // doing this and the line below will move curDir to the top regardless if it exists or not
        recentEntries.add(0, getText());
        setHistory(recentEntries);
        this.lookupKey = lookupKey;
        this.project = project;
    }

    /**
     * installs the browse functionality for the component
     * @param title the title of the browse dialog
     * @param description the subtitle (or description) given in the browse dialog
     */
    public void installBrowser(@NotNull String title, @Nullable String description) {
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        field.addBrowseFolderListener(title, description, project, descriptor);
    }

    /**
     * append a new entry to this field's history.
     * @param project the project this field is used to customize
     * @param updateAction an additional, optional trigger of other functions.
     */
    public void addToHistory(Project project, @NotNull BiConsumer<String, String> updateAction) {
        RecentsManager.getInstance(project).registerRecentEntry(lookupKey.get(), field.getText());
        updateAction.accept(field.getText(), lookupKey.get());
    }

    /**
     * @return the button icons for the built-in icon
     */
    protected Pair<Icon, Icon> getBuiltinIcons() {
        return new Pair<>(AllIcons.General.OpenDisk, AllIcons.General.OpenDiskHover);
    }
}
