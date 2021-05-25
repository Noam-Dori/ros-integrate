package ros.integrate.ui;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.refactoring.copy.CopyFilesOrDirectoriesDialog;
import com.intellij.ui.RecentsManager;
import com.intellij.ui.TextFieldWithHistory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class PathTextFieldWithHistory extends TextFieldWithHistory {

    private final TextFieldWithBrowseButton field = new TextFieldWithBrowseButton();

    public PathTextFieldWithHistory() {
        setEditable(true);
        setEditor(new BasicComboBoxEditor() {
            @Override
            protected JTextField createEditorComponent() {
                return field.getTextField();
            }
        });
    }

    public void installBrowserHistory(@NotNull BrowserOptions options) {
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        field.addBrowseFolderListener(options.title,
                options.description,
                options.project, descriptor, TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT);

        List<String> recentEntries = Optional.ofNullable(RecentsManager.getInstance(options.project)
                .getRecentEntries(options.getKey()))
                .orElse(new LinkedList<>());
        recentEntries.remove(field.getText()); // doing this and the line below will move curDir to the top regardless if it exists or not
        recentEntries.add(0, field.getText());
        setHistory(recentEntries);

        // folder text field
        FileChooserFactory.getInstance().installFileCompletion(field.getTextField(), descriptor, true, null);
        field.setTextFieldPreferredWidth(CopyFilesOrDirectoriesDialog.MAX_PATH_LENGTH);
    }
}
