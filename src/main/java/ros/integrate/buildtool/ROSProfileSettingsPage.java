package ros.integrate.buildtool;

import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.buildtool.ui.SelectableListTable;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * the user interface that allows the user to add,get, or modify the project's ROS buildtool configurations,
 * also called profiles.
 * @author Noam Dori
 */
public class ROSProfileSettingsPage implements SearchableConfigurable {
    private final ROSProfiles data;
    private final SelectableListTable profileList;
    private final Project project;
    private final Map<Integer, JPanel> profileForms = new HashMap<>();
    private Integer selectedId = null;

    /**
     * construct a new settings page
     * @param project the project this settings page belongs to
     */
    public ROSProfileSettingsPage(Project project) {
        this.project = project;
        data = ROSProfiles.getInstance(project);
        profileList = new SelectableListTable(data::requestId,
                id -> data.getProfileProperty(id, ROSProfile::getName),
                id -> data.getProfileProperty(id, ROSProfile::getIcon));
    }


    @NotNull
    @Override
    public String getId() {
        return "ROS.Profiles";
    }

    @Override
    public String getDisplayName() {
        return "ROS Profiles";
    }


    @Nullable
    @Override
    public JComponent createComponent() {
        JPanel ret = new JPanel(new GridBagLayout());

        // at the top level, there should be two things:
        // the list containing existing profiles and allows adding/removing/duplicating them
        // a form right next to it that shows a form that allows editing the selected profile

        GridBagConstraints listLayout = new GridBagConstraints();
        listLayout.weighty = 1;
        listLayout.gridx = listLayout.gridy = 0;
        listLayout.fill = GridBagConstraints.VERTICAL;
        listLayout.ipadx = 80;
        listLayout.anchor = GridBagConstraints.NORTHWEST;
        ret.add(profileList.getComponent(), listLayout);

        GridBagConstraints labelLayout = new GridBagConstraints();
        labelLayout.weightx = labelLayout.weighty = labelLayout.gridx = 1;
        labelLayout.gridy = 0;
        labelLayout.fill = GridBagConstraints.HORIZONTAL;
        labelLayout.anchor = GridBagConstraints.CENTER;
        labelLayout.insets.left = 10;
        labelLayout.insets.top = 5;

        JBLabel detailLabel = new JBLabel("Select profile to configure");
        detailLabel.setForeground(JBColor.GRAY);
        JPanel detailPanel = new JPanel();
        detailPanel.add(detailLabel);
        ret.add(detailPanel, labelLayout);

        GridBagConstraints formLayout = new GridBagConstraints();
        formLayout.weightx = formLayout.weighty = formLayout.gridx = 1;
        formLayout.gridy = 0;
        formLayout.fill = GridBagConstraints.HORIZONTAL;
        formLayout.anchor = GridBagConstraints.NORTHWEST;
        formLayout.insets.left = 10;
        formLayout.insets.top = 5;

        profileList.getTableView().getSelectionModel().addListSelectionListener(e -> {
            int newId = profileList.getTableView().getRow(e.getFirstIndex());
            if (selectedId != null && selectedId.equals(newId)) {
                return;
            }
            JPanel formToSelect = profileForms.get(newId);
            if (formToSelect == null) {
                formToSelect = new FormBuilder()
                        .addLabeledComponent(new JBLabel("Name:"), new JBTextField())
                        .addComponent(new JSeparator())
                        .getPanel();
                ret.add(formToSelect, formLayout);
                profileForms.put(newId, formToSelect);
                // TODO: 5/4/2021 read data and load into form
            }
            if (selectedId != null) {
                profileForms.get(selectedId).setVisible(false);
            } else {
                detailLabel.setVisible(false);
            }
            selectedId = newId;
            formToSelect.setVisible(true);
        });

        return ret;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() {

    }
}
