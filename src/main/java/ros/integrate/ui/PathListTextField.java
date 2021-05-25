package ros.integrate.ui;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Pair;
import com.intellij.refactoring.copy.CopyFilesOrDirectoriesDialog;
import com.intellij.ui.GuiUtils;
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
        private final BrowserOptions options;

        /**
         * constructs the new dialogue
         * @param component the parent component that called this dialogue
         * @param options an options object loading a bunch of useful settings like window title,
         *                history storage, etc.
         */
        PackagePathDialog(PathListTextField component, BrowserOptions options) {
            super(component, true);
            parent = component;
            data = new PathListTable(options);
            this.options = options;

            setTitle(options.dialogTitle);
            data.setValues(PathListUtil.parsePathList(component.getText(), options.delimiter, options.addBrowser));
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

            parent.setPaths(data.getPaths(), options.delimiter);

            super.doOKAction();
        }
    }

    protected void installBrowseButton(@NotNull BrowserOptions options) {
        field.addActionListener(actionEvent -> new PackagePathDialog(this, options).show());
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
