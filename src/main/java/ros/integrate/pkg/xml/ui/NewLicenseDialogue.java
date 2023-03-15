package ros.integrate.pkg.xml.ui;

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
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.PsiManager;
import com.intellij.refactoring.RefactoringBundle;
import com.intellij.refactoring.copy.CopyFilesOrDirectoriesDialog;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.ui.*;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.io.File;
import java.time.Year;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * the user interface dialogue that allows the user to fill information used in license templates
 * @author Noam Dori
 */
public class NewLicenseDialogue extends DialogWrapper {

    private final String TARGET_DIR = "NewLicense.FILE_NAME", COPYRIGHT_HOLDER = "NewLicense.COPYRIGHT_HOLDER";

    private final TextFieldWithHistoryWithBrowseButton targetDirField = new TextFieldWithHistoryWithBrowseButton();
    private final JLabel targetDirLabel = new JBLabel();

    private final JBTextField fileName = new JBTextField();
    private final JLabel fileNameLabel = new JLabel();

    private final TextFieldWithHistory copyrightHolder = new TextFieldWithHistory();
    private final JLabel copyrightHolderLabel = new JLabel();

    private final JBIntSpinner licenseYear = new JBIntSpinner(Year.now().getValue(), 1900, 2100);
    private final JLabel licenseYearLabel = new JLabel();

    private PsiDirectory targetDir;
    private final Project project;
    private final RecentsManager recentsManager;

    /**
     * constructs a new dialogue
     * @param origDirectory the directory the calling package.xml file belongs to
     */
    public NewLicenseDialogue(@NotNull PsiDirectory origDirectory) {
        super(origDirectory.getProject());
        project = origDirectory.getProject();
        targetDir = origDirectory;
        recentsManager = RecentsManager.getInstance(project);

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
        String browserTitle = "Choose Target Directory";
        String browserDescription = "The License file will be created here";

        setTitle("Create ROS Message");

        targetDirLabel.setText("Destination folder:");
        fileNameLabel.setText("Name:");
        copyrightHolderLabel.setText("Copyright holder:");
        licenseYearLabel.setText("License year:");

        fileName.setText(findPossibleName());
        installHistory(targetDirField.getChildComponent(), TARGET_DIR, targetDir.getVirtualFile().getPath());
        installHistory(copyrightHolder, COPYRIGHT_HOLDER, System.getenv("username"));

        DocumentAdapter validateOk = new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                validateOKButton();
            }
        };

        // folder browser
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        targetDirField.addBrowseFolderListener(browserTitle, browserDescription,
                project, descriptor, TextComponentAccessors.TEXT_FIELD_WITH_HISTORY_WHOLE_TEXT);

        // folder completion?
        final JTextField textField = targetDirField.getChildComponent().getTextEditor();
        FileChooserFactory.getInstance().installFileCompletion(textField, descriptor, true, getDisposable());
        textField.getDocument().addDocumentListener(validateOk);
        targetDirField.setTextFieldPreferredWidth(CopyFilesOrDirectoriesDialog.MAX_PATH_LENGTH);


        String shortcutText = KeymapUtil.getFirstKeyboardShortcutText(
                ActionManager.getInstance().getAction(IdeActions.ACTION_CODE_COMPLETION));

        fileName.getDocument().addDocumentListener(validateOk);
        targetDirField.getChildComponent().getTextEditor().getDocument().addDocumentListener(validateOk);
        copyrightHolder.getTextEditor().getDocument().addDocumentListener(validateOk);
        licenseYear.addChangeListener(e -> validateOKButton());
        validateOKButton();
        // grand return
        return FormBuilder.createFormBuilder()
                .addLabeledComponent(fileNameLabel, fileName)
                .addLabeledComponent(copyrightHolderLabel, copyrightHolder)
                .addLabeledComponent(licenseYearLabel, licenseYear)
                .addComponent(targetDirLabel)
                .addComponent(targetDirField)
                .addTooltip(RefactoringBundle.message("path.completion.shortcut", shortcutText))
                .getPanel();
    }

    @NotNull
    private String findPossibleName() {
        Pattern r = Pattern.compile("^LICENSE(?:_(\\d+))?");
        Set<Integer> count = Arrays.stream(targetDir.getFiles())
                .map(PsiFileSystemItem::getName)
                .map(r::matcher)
                .filter(Matcher::find)
                .map(m -> m.group(1) == null ? 0 : Integer.parseInt(m.group(1)))
                .collect(Collectors.toSet());
        if (count.isEmpty()) {
            return "LICENSE.txt";
        }
        int retIfDense = Collections.max(count);
        Set<Integer> range = IntStream.rangeClosed(0,
                        retIfDense++).boxed().collect(Collectors.toSet());
        range.removeAll(count);
        return "LICENSE" +
                range.stream().findFirst().map(num -> num == 0 ? "" : "_" + num).orElse("_" + retIfDense) +
                ".txt";
    }

    private void validateOKButton() {
        setOKActionEnabled(!fileName.getText().isEmpty() &&
                !targetDirField.getChildComponent().getText().isEmpty() &&
                !copyrightHolder.getText().isEmpty() &&
                licenseYear.getNumber() > 1900);
    }

    @Override
    protected void doOKAction() {
        RecentsManager.getInstance(project).registerRecentEntry(TARGET_DIR, targetDirField.getChildComponent().getText());
        RecentsManager.getInstance(project).registerRecentEntry(COPYRIGHT_HOLDER, copyrightHolder.getText());

        if (DumbService.isDumb(project)) {
            Messages.showMessageDialog(project, "Move refactoring is not available while indexing is in progress", "Indexing", null);
            return;
        }

        // sets targetDir to chosen dir, and creates extra directories if needed.
        CommandProcessor.getInstance().executeCommand(project, () -> {
            final Runnable action = () -> {
                String directoryName = targetDirField.getChildComponent().getText().replace(File.separatorChar, '/');
                try {
                    targetDir = DirectoryUtil.mkdirs(PsiManager.getInstance(project), directoryName);
                }
                catch (IncorrectOperationException ignored) {}
            };

            ApplicationManager.getApplication().runWriteAction(action);
            if (targetDir == null) {
                CommonRefactoringUtil.showErrorMessage(getTitle(),
                        RefactoringBundle.message("cannot.create.directory"), null, project);
                return;
            }

            close(OK_EXIT_CODE);
        }, "Creating New File", null);
    }

    private void installHistory(@NotNull TextFieldWithHistory field, @NotNull String key, @NotNull String curText) {
        List<String> recentEntries = Optional.ofNullable(recentsManager.getRecentEntries(key))
                .orElse(new LinkedList<>());
        recentEntries.remove(curText);
        recentEntries.add(0, curText);
        field.setHistory(recentEntries);
        field.setText(curText);
    }

    /**
     * @return the name the new license file should have
     */
    public String getFileName() {
        return fileName.getText();
    }

    /**
     * @return the directory this license file should be placed in
     */
    public PsiDirectory getDirectory() {
        return targetDir;
    }

    /**
     * @return the year this license was drafted for. Usually this is just this year
     */
    public String getYear() {
        return String.valueOf(licenseYear.getNumber());
    }

    /**
     * @return the name of the copyright holder. This can be a person, a company, an organisation, etc.
     */
    public String getCopyrightHolder() {
        return copyrightHolder.getText();
    }
}
