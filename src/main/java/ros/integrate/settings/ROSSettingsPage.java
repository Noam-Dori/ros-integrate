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
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class ROSSettingsPage implements Configurable {
    @SuppressWarnings("FieldCanBeLocal")
    private final String RECENT_KEYS = "ROSSettings.RECENT_KEYS";

    private final Project project;

    private final JBLabel rosSettingsLabel = new JBLabel();

    private final JSeparator envVariables = new JSeparator();
    private final JBLabel envVariablesLabel = newSectionHeaderLabel();

    private final TextFieldWithHistoryWithBrowseButton rosRoot = new TextFieldWithHistoryWithBrowseButton();
    private final JBLabel rosRootLabel = new JBLabel();

    public ROSSettingsPage(Project project) {
        this.project = project;
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
        rosSettingsLabel.setText("In here, you can configure your interactions with ROS in the IDE");
        envVariablesLabel.setText("Environment Variables");
        rosRootLabel.setText("ROS Root");

        initRosRoot("Choose Target Directory","This Directory is the Root ROS Library.");

        JPanel unalignedPanel = FormBuilder.createFormBuilder()
                .addComponent(rosSettingsLabel)
                .addLabeledComponent(envVariablesLabel, envVariables, UIUtil.LARGE_VGAP)
                .addLabeledComponent(rosRootLabel,rosRoot)
                .getPanel();
        JPanel ret = new JPanel(new BorderLayout());
        ret.add(unalignedPanel, BorderLayout.NORTH);
        return ret;
    }

    @SuppressWarnings("SameParameterValue") // its nicer from a configuration standpoint.
    private void initRosRoot(String browserTitle, String browserDescription) {
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        rosRoot.addBrowseFolderListener(browserTitle,
                browserDescription,
                project, descriptor, TextComponentAccessor.TEXT_FIELD_WITH_HISTORY_WHOLE_TEXT);

        List<String> recentEntries = RecentsManager.getInstance(project).getRecentEntries(RECENT_KEYS);
        if (recentEntries == null) {
            recentEntries = new LinkedList<>();
        }
        String curDir = ROSSettings.getInstance(project).getROSPath();
        recentEntries.remove(curDir); // doing this and the line below will move curDir to the top regardless if it exists or not
        recentEntries.add(0,curDir);
        rosRoot.getChildComponent().setHistory(recentEntries);

        // add current PSI dir as current selection
        rosRoot.getChildComponent().setText(curDir);

        // folder text field
        final JTextField textField = rosRoot.getChildComponent().getTextEditor();
        FileChooserFactory.getInstance().installFileCompletion(textField, descriptor, true, null);
        rosRoot.setTextFieldPreferredWidth(CopyFilesOrDirectoriesDialog.MAX_PATH_LENGTH);
    }

    @Override
    public boolean isModified() {
        ROSSettings data = ROSSettings.getInstance(project);
        return isModified(rosRoot.getChildComponent().getTextEditor(),data.getROSPath());
    }

    @Override
    public void apply() {
        boolean triggerFlag = false;
        ROSSettings data = ROSSettings.getInstance(project);
        if(!rosRoot.getText().isEmpty()) {
            // adds to history
            RecentsManager.getInstance(project).registerRecentEntry(RECENT_KEYS, rosRoot.getChildComponent().getText());

            data.setRosPath(rosRoot.getText());
            triggerFlag = true;
        }

        if(triggerFlag) {
            data.triggerListeners();
        }
    }

    @Override
    public void reset() {
        ROSSettings data = ROSSettings.getInstance(project);
        rosRoot.setText(data.getROSPath());
    }


}
