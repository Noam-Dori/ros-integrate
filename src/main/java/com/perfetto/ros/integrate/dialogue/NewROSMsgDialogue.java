package com.perfetto.ros.integrate.dialogue;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.util.Disposer;
import com.intellij.psi.PsiDirectory;
import com.intellij.refactoring.RefactoringBundle;
import com.intellij.refactoring.copy.CopyFilesOrDirectoriesDialog;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.RecentsManager;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class NewROSMsgDialogue extends DialogWrapper {

    private final String RECENT_KEYS = "NewMsg.RECENT_KEYS";

    private final JTextField msgNameField = new MyTextField();
    private final JLabel msgNameLabel = new JLabel("Name:"),
            targetDirLabel = new JBLabel("Destination Folder:");
    private final TextFieldWithHistoryWithBrowseButton targetDirField = new TextFieldWithHistoryWithBrowseButton();
    private final PsiDirectory targetDir;
    private final Project prj;

    public NewROSMsgDialogue(@NotNull Project prj, @Nullable PsiDirectory suggestedDir, @Nullable String suggestedName) {
        super(prj);
        this.prj = prj;

        if(suggestedDir != null) {
            targetDir = suggestedDir;
        } else {
            targetDir = (PsiDirectory) prj.getBaseDir();
        }

        setTitle("Create ROS Message");
        init();

        msgNameField.setText(suggestedName);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return new JPanel(new BorderLayout());
    }

    @Nullable
    @Override
    protected JComponent createNorthPanel() {

        // folder browser history
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        targetDirField.addBrowseFolderListener("Choose Target Directory",
                "The ROS Message file will be created here",
                prj, descriptor, TextComponentAccessor.TEXT_FIELD_WITH_HISTORY_WHOLE_TEXT);

        List<String> recentEntries = RecentsManager.getInstance(prj).getRecentEntries(RECENT_KEYS);
        if (recentEntries == null) {
            recentEntries = new LinkedList<>();
        }
        String curDir = targetDir.getVirtualFile().getPath();
        recentEntries.add(curDir);
        targetDirField.getChildComponent().setHistory(recentEntries);

        // add current PSI dir as current selection
        targetDirField.getChildComponent().setText(curDir);

        // folder text field
        final JTextField textField = targetDirField.getChildComponent().getTextEditor();
        FileChooserFactory.getInstance().installFileCompletion(textField, descriptor, true, getDisposable());
        textField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                validateOKButton();
            }
        });
        targetDirField.setTextFieldPreferredWidth(CopyFilesOrDirectoriesDialog.MAX_PATH_LENGTH);

        // autocompletion
        // FIXME: 8/14/2018 the autocompletion never worked. look @ MoveFilesOrDirectoriesDialog for reference
        FileChooserFactory.getInstance().installFileCompletion(textField, descriptor, true, getDisposable());
        textField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                validateOKButton();
            }
        });
        Disposer.register(getDisposable(), targetDirField);
        String shortcutText = KeymapUtil.getFirstKeyboardShortcutText(
                ActionManager.getInstance().getAction(IdeActions.ACTION_CODE_COMPLETION));

        // grand return
        return FormBuilder.createFormBuilder()
                .addLabeledComponent(msgNameLabel,msgNameField)
                .addLabeledComponentFillVertically(targetDirLabel.getText(),targetDirField)
                .addTooltip(RefactoringBundle.message("path.completion.shortcut", shortcutText))
                .getPanel();
    }

    private void validateOKButton() {
        setOKActionEnabled(targetDirField.getChildComponent().getText().length() > 0);
    }

    //TODO: override OK action just like MoveFilesOrDirectoriesDialog

    public String getFileName() {
        return msgNameField.getText();
    }

    public PsiDirectory getDirectory() {
        return targetDir;
    }

    private static class MyTextField extends JTextField {
        @Override
        public Dimension getPreferredSize() {
            Dimension size = super.getPreferredSize();
            FontMetrics fontMetrics = getFontMetrics(getFont());
            size.width = fontMetrics.charWidth('a') * 40;
            return size;
        }
    }
}
