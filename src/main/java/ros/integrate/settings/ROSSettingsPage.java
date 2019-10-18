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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class ROSSettingsPage implements Configurable {
    private enum HistoryKey {
        DEFAULT("RECENT_KEYS"),
        WORKSPACE("WORKSPACE");

        private final String historyKey;

        @Contract(pure = true)
        HistoryKey(String lookupKey) {
            this.historyKey = "ROSSettings." + lookupKey;
        }

        @Contract(pure = true)
        public String get() {
            return historyKey;
        }
    }

    private static class BrowserOptions {
        private String title = "", description = "";
        private HistoryKey key = HistoryKey.DEFAULT;
        @Contract(pure = true)
        BrowserOptions() {}

        @Contract(pure = true)
        BrowserOptions(HistoryKey historyKey) {
            this.key = historyKey;
        }

        BrowserOptions withDescription(String description) {
            this.description = description;
            return this;
        }

        BrowserOptions withTitle(String title) {
            this.title = title;
            return this;
        }

        String getKey() {
            return key.get();
        }
    }

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
        rosRootLabel.setText("ROS Path");
        workspaceLabel.setText("Workspace");

        installBrowserHistory(rosRoot,new BrowserOptions()
                .withTitle("Choose Target Directory")
                .withDescription("This Directory is the Root ROS Library."));
        installBrowserHistory(workspace,new BrowserOptions(HistoryKey.WORKSPACE)
                .withTitle("Choose Target Workspace")
                .withDescription("This is the root directory of this project's workspace"));

        JPanel unalignedPanel = FormBuilder.createFormBuilder()
                .addComponent(rosSettingsLabel)
                .addLabeledComponent(envVariablesLabel, envVariables, UIUtil.LARGE_VGAP)
                .addLabeledComponent(rosRootLabel,rosRoot)
                .addLabeledComponent(workspaceLabel, workspace)
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

        List<String> recentEntries = recentsManager.getRecentEntries(options.getKey());
        if (recentEntries == null) {
            recentEntries = new LinkedList<>();
        }
        String curDir = field.getText();
        recentEntries.remove(curDir); // doing this and the line below will move curDir to the top regardless if it exists or not
        recentEntries.add(0,curDir);
        field.getChildComponent().setHistory(recentEntries);

        // add current PSI dir as current selection
        field.getChildComponent().setText(curDir);

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

        if(triggerFlag) {
            data.triggerListeners();
        }
    }

    @Override
    public void reset() {
        rosRoot.setText(data.getROSPath());
        workspace.setText(data.getWorkspacePath());
    }


}
