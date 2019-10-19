package ros.integrate.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.refactoring.copy.CopyFilesOrDirectoriesDialog;
import com.intellij.ui.RecentsManager;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.Consumer;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.settings.BrowserOptions.HistoryKey;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ROSSettingsPage implements Configurable {

    private final Project project;
    private final RecentsManager recentsManager;
    private final ROSSettings data;

    private final JBLabel rosSettingsLabel = new JBLabel();

    private final JSeparator envVariables = new JSeparator();
    private final JBLabel envVariablesLabel = newSectionHeaderLabel();

    private final TextFieldWithHistoryWithBrowseButton rosRoot = new TextFieldWithHistoryWithBrowseButton();
    private final JBLabel rosRootLabel = new JBLabel();

    private final TextFieldWithHistoryWithBrowseButton workspace = new TextFieldWithHistoryWithBrowseButton();
    private final JBLabel workspaceLabel = new JBLabel();

    private final PathListTextField additionalSources = new PathListTextField();
    private final JBLabel additionalSourcesLabel = new JBLabel();

    public ROSSettingsPage(Project project) {
        this.project = project;
        recentsManager = RecentsManager.getInstance(project);
        data = ROSSettings.getInstance(project);
    }

    @NotNull
    private JBLabel newSectionHeaderLabel() {
        JBLabel ret = new JBLabel();
        Font oldFont = ret.getFont();
        ret.setFont(new Font(oldFont.getName(),oldFont.getStyle(),oldFont.getSize() - 2));
        return ret;
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "ROS";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        reset();
        rosSettingsLabel.setText("In here, you can configure your interactions with ROS in the IDE");
        envVariablesLabel.setText("Environment");
        rosRootLabel.setText("ROS Path:");
        workspaceLabel.setText("Workspace:");
        additionalSourcesLabel.setText("Additional Package Repositories:");

        installBrowserHistory(rosRoot, new BrowserOptions(project)
                .withTitle("Choose Target Directory")
                .withDescription("This Directory is the Root ROS Library."));
        installBrowserHistory(workspace, new BrowserOptions(project, HistoryKey.WORKSPACE)
                .withTitle("Choose Target Workspace")
                .withDialogTitle("Configure Path to Workspace")
                .withDescription("This is the root directory of this project's workspace"));
        additionalSources.installHistoryAndDialog(recentsManager, new BrowserOptions(project, HistoryKey.EXTRA_SOURCES)
                .withTitle("Modify source path")
                .withDialogTitle("Configure Paths to Source")
                .withDescription("This is the a root directory to additional sources outside of the workspace."));

        JPanel unalignedPanel = FormBuilder.createFormBuilder()
                .addComponent(rosSettingsLabel)
                .addLabeledComponent(envVariablesLabel, envVariables, UIUtil.LARGE_VGAP)
                .addLabeledComponent(rosRootLabel, rosRoot)
                .addLabeledComponent(workspaceLabel, workspace)
                .addLabeledComponent(additionalSourcesLabel, additionalSources)
                .getPanel();
        JPanel ret = new JPanel(new BorderLayout());
        ret.add(unalignedPanel, BorderLayout.NORTH);
        return ret;
    }

    private void installBrowserHistory(@NotNull TextFieldWithHistoryWithBrowseButton field, @NotNull BrowserOptions options) {
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        field.addBrowseFolderListener(options.title,
                options.description,
                project, descriptor, TextComponentAccessor.TEXT_FIELD_WITH_HISTORY_WHOLE_TEXT);

        List<String> recentEntries = Optional.ofNullable(recentsManager.getRecentEntries(options.getKey()))
                .orElse(new LinkedList<>());
        recentEntries.remove(field.getText()); // doing this and the line below will move curDir to the top regardless if it exists or not
        recentEntries.add(0,field.getText());
        field.getChildComponent().setHistory(recentEntries);

        // folder text field
        final JTextField textField = field.getChildComponent().getTextEditor();
        FileChooserFactory.getInstance().installFileCompletion(textField, descriptor, true, null);
        field.setTextFieldPreferredWidth(CopyFilesOrDirectoriesDialog.MAX_PATH_LENGTH);
    }

    @Override
    public boolean isModified() {
        return isModified(rosRoot.getChildComponent().getTextEditor(),data.getROSPath());
    }

    private boolean addToHistory(@NotNull TextFieldWithHistoryWithBrowseButton field, HistoryKey historyKey, Consumer<String> updateAction) {
        if (!field.getText().isEmpty()) {
            recentsManager.registerRecentEntry(historyKey.get(), rosRoot.getChildComponent().getText());
            updateAction.consume(field.getText());
            return true;
        }
        return false;
    }

    @Override
    public void apply() {
        boolean triggerFlag = addToHistory(rosRoot,HistoryKey.DEFAULT, data::setRosPath);
        triggerFlag |= addToHistory(workspace,HistoryKey.WORKSPACE, data::setWorkspacePath);
        triggerFlag |= addToHistory(additionalSources,HistoryKey.EXTRA_SOURCES, data::setAdditionalSources);

        if(triggerFlag) {
            data.triggerListeners();
        }
    }

    @Override
    public void reset() {
        rosRoot.setText(data.getROSPath());
        workspace.setText(data.getWorkspacePath());
        additionalSources.setText(data.getRawAdditionalSources());
    }

}
