package ros.integrate.ui;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Pair;
import com.intellij.refactoring.copy.CopyFilesOrDirectoriesDialog;
import com.intellij.ui.GuiUtils;
import com.intellij.ui.TextFieldWithAutoCompletionListProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.ROSIcons;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * a text field meant for storing lists of paths. When clicking on ..., it will open the path list editor.
 * This field also saves history
 * @author Noam Dori
 */
public class PathListTextField extends PathTextFieldWithHistory {
    /**
     * the dialogue wrapper that pops up when clicking on ...
     * this implements the OK button and uses the path list table
     */
    private static class PackagePathDialog extends DialogWrapper {
        private final JPanel display = new JPanel(new BorderLayout());
        private final PathListTextField parent;

        private final PathListTable data;
        private final char delimiter;

        /**
         * constructs the new dialogue
         * @param component the parent component that called this dialogue
         * @param delimiter the character that splits between entries in the text form
         * @param browserTitle the title of the browse dialog
         * @param browserDescription the subtitle (or description) given in the browse dialog
         * @param dialogTitle the title of this dialog window
         */
        PackagePathDialog(PathListTextField component, char delimiter, String dialogTitle,
                          @Nullable String browserTitle, @Nullable String browserDescription,
                          @Nullable TextFieldWithAutoCompletionListProvider<String> completionEngine) {
            super(component, true);
            parent = component;
            data = new PathListTable(component.project, browserTitle, browserDescription, completionEngine);
            this.delimiter = delimiter;

            setTitle(dialogTitle);
            data.setValues(PathListUtil.parsePathList(component.getText(), delimiter, browserTitle != null));
            display.add(data.getComponent(),BorderLayout.CENTER);
            Dimension oneRow = GuiUtils.getSizeByChars(CopyFilesOrDirectoriesDialog.MAX_PATH_LENGTH, display);
            display.setPreferredSize(new Dimension(oneRow.width, oneRow.height * 3));
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

            parent.setPaths(data.getPaths(), delimiter);

            super.doOKAction();
        }
    }

    @Nullable
    private String browserTitle = null, browserDescription = null;
    @Nullable
    private TextFieldWithAutoCompletionListProvider<String> completionEngine = null;

    @Override
    public void installBrowser(@NotNull String title, String description) {
        this.browserTitle = title;
        this.browserDescription = description;
    }

    /**
     * installs the list expansion.
     * @param dialogTitle the title of the result dialog window
     * @param delimiter the character that splits between entries in the text form
     */
    public void installListExpansion(String dialogTitle, char delimiter) {
        field.addActionListener(actionEvent ->
                new PackagePathDialog(this, delimiter, dialogTitle, browserTitle, browserDescription,
                        completionEngine).show());
    }

    public void installListExpansion(String dialogTitle) {
        installListExpansion(dialogTitle, ':');
    }


    /**
     * install an autocompletion feature to the cell entries
     * @param completionEngine a provider that gives a lookup of possible string entries
     */
    public void installAutoCompletion(TextFieldWithAutoCompletionListProvider<String> completionEngine) {
        this.completionEngine = completionEngine;
    }

    /**
     * changes the text value stored in the text field
     * @param paths the list of paths to be serialized and placed in the field
     * @param delimiter a delimiter to put between the paths
     */
    private void setPaths(@NotNull List<String> paths, char delimiter) {
        setText(PathListUtil.serializePathList(paths, delimiter));
    }

    @Override
    protected Pair<Icon, Icon> getBuiltinIcons() {
        return new Pair<>(ROSIcons.LIST_FILES, ROSIcons.LIST_FILES_HOVER);
    }
}
