package ros.integrate.pkt.dialogue;

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
import com.intellij.ui.JBColor;
import com.intellij.ui.RecentsManager;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.annotate.ROSPktTypeAnnotator;
import ros.integrate.pkt.inspection.CamelCaseInspection;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * the message dialog when creating a new message type.
 */
public class NewROSMsgDialogue extends DialogWrapper {
    // TODO: 8/29/2018 add error annotation for illegal message types somehow. spelling would be nice too

    private final String RECENT_KEYS = "NewMsg.RECENT_KEYS";

    private final JBTextField msgNameField = new MyTextField();
    private final JBLabel msgNameTooltip = createToolTip();
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

        msgNameField.setText(suggestedName);
        msgNameField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) { validateOKButton(); updateMsgNameTooltip();
            }
        });

        setTitle("Create ROS Message");
        init();
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

        validateOKButton();
        updateMsgNameTooltip();
        // grand return
        return FormBuilder.createFormBuilder()
                .addLabeledComponent(msgNameLabel,msgNameField)
                .addComponentToRightColumn(msgNameTooltip,1)
                .addLabeledComponentFillVertically(targetDirLabel.getText(),targetDirField)
                .addTooltip(RefactoringBundle.message("path.completion.shortcut", shortcutText))
                .getPanel();
    }

    private JBLabel createToolTip() {
        final JBLabel label = new JBLabel("", UIUtil.ComponentStyle.SMALL);
        label.setBorder(JBUI.Borders.emptyLeft(10));
        return label;
    }

    private void validateOKButton() {
        boolean nameValid = ROSPktTypeAnnotator.getIllegalTypeMessage(msgNameField.getText(),true) == null;
        boolean targetDirValid = targetDirField.getChildComponent().getText().length() > 0;
        setOKActionEnabled(nameValid && targetDirValid);
    }

    private void updateMsgNameTooltip() {
        String message = ROSPktTypeAnnotator.getIllegalTypeMessage(msgNameField.getText(),true);
        if (message == null) {
            message = CamelCaseInspection.getUnorthodoxTypeMessage(msgNameField.getText(),true);
            if (message == null) {
                msgNameTooltip.setText(" ");
            } else {
                msgNameTooltip.setForeground(new JBColor(0xC4A000,0xEFBF6A));
                msgNameTooltip.setText(message);
            }
        } else {
            msgNameTooltip.setForeground(JBColor.RED);
            msgNameTooltip.setText(message);
        }
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

    private static class MyTextField extends JBTextField {
        @Override
        public Dimension getPreferredSize() {
            Dimension size = super.getPreferredSize();
            FontMetrics fontMetrics = getFontMetrics(getFont());
            size.width = fontMetrics.charWidth('a') * 40;
            return size;
        }
    }
}
