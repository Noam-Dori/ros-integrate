package ros.integrate.msg.dialogue;

import com.intellij.ide.util.DirectoryUtil;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.util.Disposer;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.refactoring.RefactoringBundle;
import com.intellij.refactoring.copy.CopyFilesOrDirectoriesDialog;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.RecentsManager;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class NewROSMsgDialogue extends DialogWrapper {

    private final String RECENT_KEYS = "NewMsg.RECENT_KEYS";

    private final JTextField msgNameField = new MyTextField();
    private final JLabel msgNameLabel = new JLabel("Name:"),
            targetDirLabel = new JBLabel("Destination Folder:");
    private final TextFieldWithHistoryWithBrowseButton targetDirField = new TextFieldWithHistoryWithBrowseButton();
    private PsiDirectory targetDir;
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
        recentEntries.remove(curDir); // doing this and the line below will move curDir to the top regardless if it exists or not
        recentEntries.add(0,curDir);
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

    @Override
    protected void doOKAction() {

        // adds history
        RecentsManager.getInstance(prj).registerRecentEntry(RECENT_KEYS, targetDirField.getChildComponent().getText());

        if (DumbService.isDumb(prj)) {
            Messages.showMessageDialog(prj, "Move refactoring is not available while indexing is in progress", "Indexing", null);
            return;
        }

        // sets targetDir to chosen dir
        CommandProcessor.getInstance().executeCommand(prj, () -> {
            final Runnable action = () -> {
                String directoryName = targetDirField.getChildComponent().getText().replace(File.separatorChar, '/');
                try {
                    targetDir = DirectoryUtil.mkdirs(PsiManager.getInstance(prj), directoryName);
                }
                catch (IncorrectOperationException e) {
                    // ignore
                }
            };

            ApplicationManager.getApplication().runWriteAction(action);
            if (targetDir == null) {
                CommonRefactoringUtil.showErrorMessage(getTitle(),
                        RefactoringBundle.message("cannot.create.directory"), null, prj);
                return;
            }

            close(OK_EXIT_CODE);
        }, "New ROS Message", null);
    }

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
