package ros.integrate.pkt.dialog;

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
import com.intellij.openapi.ui.TextComponentAccessors;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.refactoring.RefactoringBundle;
import com.intellij.refactoring.copy.CopyFilesOrDirectoriesDialog;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.spellchecker.SpellCheckerManager;
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
import ros.integrate.pkg.ROSPackageManager;
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.pkg.psi.impl.ROSSourcePackage;
import ros.integrate.pkt.annotate.ROSPktTypeAnnotator;
import ros.integrate.pkt.inspection.CamelCaseInspection;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * the user interface dialog that allows the user to fill information used when creating a new message type.
 * @author Noam Dori
 */
public class NewROSMsgDialog extends DialogWrapper {

    private final String RECENT_KEYS = "NewMsg.RECENT_KEYS";

    private final JBTextField msgNameField = new JBTextField();
    private final JBLabel msgNameTooltip = createToolTip();
    private final JLabel msgNameLabel = new JLabel("Name:"),
            targetDirLabel = new JBLabel("Destination folder:");
    private final TextFieldWithHistoryWithBrowseButton targetDirField = new TextFieldWithHistoryWithBrowseButton();
    private PsiDirectory targetDir;
    private final Project project;
    @NotNull
    private static final List<BiFunction<String, Project, Pair<String, JBColor>>> tooltips = getTooltipGenerators();

    @NotNull
    private static List<BiFunction<String, Project, Pair<String, JBColor>>> getTooltipGenerators() {
        List<BiFunction<String, Project, Pair<String, JBColor>>> ret = new LinkedList<>();
        ret.add((text, project) -> Optional.ofNullable(ROSPktTypeAnnotator.getIllegalTypeMessage(text, true))
                .map(message -> new Pair<>(message, JBColor.RED)).orElse(null));
        ret.add((text, project) -> Optional.ofNullable(CamelCaseInspection.getUnorthodoxTypeMessage(text, true))
                .map(message -> new Pair<>(message, new JBColor(0xC4A000,0xEFBF6A))).orElse(null));
        ret.add((text, project) -> SpellCheckerManager.getInstance(project).hasProblem(text) ?
                new Pair<>("Typo: In word '" + text + "'", new JBColor(0x659C6B, 0xB0D1AB)) : null);
        ret.add((text, project) -> new Pair<>(" ", JBColor.BLACK));
        return ret;
    }

    /**
     * construct the dialogue
     * @param project the project this dialog belongs to
     * @param suggestedPkg the initial package the interface should suggest to place the message in
     * @param suggestedName the initial name this message should have.
     *                      This can still be changed by the user in the dialogue
     * @param callingFile the original file that triggered the dialogue
     */
    public NewROSMsgDialog(@NotNull Project project, @Nullable ROSPackage suggestedPkg, @Nullable String suggestedName,
                           @NotNull PsiFile callingFile) {
        super(project);
        this.project = project;

        targetDir = suggestedPkg == null || !suggestedPkg.isEditable() || suggestedPkg.getMsgRoot() == null ?
                callingFile.getContainingDirectory() : suggestedPkg.getMsgRoot();
        if (targetDir == null) {
            targetDir = project.getService(ROSPackageManager.class).getAllPackages()
                    .stream().filter(pkg -> pkg instanceof ROSSourcePackage)
                    .collect(Collectors.toList()).get(0).getMsgRoot();
        }

        msgNameField.setText(suggestedName);
        msgNameField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                validateOKButton(); updateMsgNameTooltip();
            }
        });

        setTitle("Create ROS Message");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        // folder browser history
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        String curDir = targetDir.getVirtualFile().getPath();

        installHistory(descriptor, curDir);
        // add current PSI dir as current text
        targetDirField.getChildComponent().setText(curDir);

        installFileCompletion(descriptor);

        String shortcutText = KeymapUtil.getFirstKeyboardShortcutText(
                ActionManager.getInstance().getAction(IdeActions.ACTION_CODE_COMPLETION));

        validateOKButton();
        updateMsgNameTooltip();
        // grand return
        return FormBuilder.createFormBuilder()
                .addLabeledComponent(msgNameLabel,msgNameField)
                .addComponentToRightColumn(msgNameTooltip,1)
                .addComponent(targetDirLabel)
                .addComponent(targetDirField)
                .addTooltip(RefactoringBundle.message("path.completion.shortcut", shortcutText))
                .getPanel();
    }

    private void installFileCompletion(FileChooserDescriptor descriptor) {
        final JTextField textField = targetDirField.getChildComponent().getTextEditor();
        FileChooserFactory.getInstance().installFileCompletion(textField, descriptor, true, getDisposable());
        textField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                validateOKButton();
            }
        });
        targetDirField.setTextFieldPreferredWidth(CopyFilesOrDirectoriesDialog.MAX_PATH_LENGTH);

        Disposer.register(getDisposable(), targetDirField);
    }

    void installHistory(FileChooserDescriptor descriptor, String currentEntry) {
        targetDirField.addBrowseFolderListener("Choose Target Directory",
                "The ROS Message file will be created here",
                project, descriptor, TextComponentAccessors.TEXT_FIELD_WITH_HISTORY_WHOLE_TEXT);

        List<String> recentEntries = Optional.ofNullable(RecentsManager.getInstance(project).getRecentEntries(RECENT_KEYS))
                        .orElse(new LinkedList<>());

        // moves the current entry to the top of the history list
        recentEntries.remove(currentEntry);
        recentEntries.add(0,currentEntry);
        targetDirField.getChildComponent().setHistory(recentEntries);
    }

    @NotNull
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
        for (BiFunction<String, Project, Pair<String, JBColor>> tooltip : tooltips) {
            Pair<String, JBColor> result = tooltip.apply(msgNameField.getText(), project);
            if (result != null) {
                msgNameTooltip.setForeground(result.second);
                msgNameTooltip.setText(result.first);
                break;
            }
        }
    }

    @Override
    protected void doOKAction() {

        // adds history
        RecentsManager.getInstance(project).registerRecentEntry(RECENT_KEYS, targetDirField.getChildComponent().getText());

        if (DumbService.isDumb(project)) {
            Messages.showMessageDialog(project,
                    "Move refactoring is not available while indexing is in progress", "Indexing", null);
            return;
        }

        // sets targetDir to chosen dir
        CommandProcessor.getInstance().executeCommand(project, () -> {
            final Runnable action = () -> {
                String directoryName = targetDirField.getChildComponent().getText().replace(File.separatorChar, '/');
                try {
                    targetDir = DirectoryUtil.mkdirs(PsiManager.getInstance(project), directoryName);
                }
                catch (IncorrectOperationException e) {
                    // ignore
                }
            };

            ApplicationManager.getApplication().runWriteAction(action);
            if (targetDir == null) {
                CommonRefactoringUtil.showErrorMessage(getTitle(),
                        RefactoringBundle.message("cannot.create.directory"), null, project);
                return;
            }

            close(OK_EXIT_CODE);
        }, "New ROS Message", null);
    }

    /**
     * @return the name of the message file to be created
     */
    public String getFileName() {
        return msgNameField.getText();
    }

    /**
     * @return the target directory to place the new message in
     */
    public PsiDirectory getDirectory() {
        return targetDir;
    }
}
