package ros.integrate.buildtool;

import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.buildtool.ui.ROSProfileForm;
import ros.integrate.buildtool.ui.SelectableListTable;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * the user interface that allows the user to add,get, or modify the project's ROS buildtool configurations,
 * also called profiles.
 * @author Noam Dori
 */
public class ROSProfileSettingsPage implements SearchableConfigurable {
    private final ROSProfiles data;
    private final SelectableListTable profileList;
    private final Map<Integer, ROSProfileForm> profileForms = new HashMap<>();
    @NotNull
    private final Project project;
    private Integer selectedId = null;
    private List<Integer> existingProfiles;

    /**
     * construct a new settings page
     * @param project the project this settings page belongs to
     */
    public ROSProfileSettingsPage(@NotNull Project project) {
        this.project = project;
        data = ROSProfiles.getInstance(project);
        profileList = new SelectableListTable(data::requestId,
                id -> data.getProfileProperty(id, ROSProfile::getGuiName),
                id -> data.getProfileProperty(id, profile -> profile.getGuiBuildtool().getIcon()));
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

        profileList.getTableView().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        profileList.getTableView().getSelectionModel().addListSelectionListener(e -> {
            int row = profileList.getTableView().getSelectedRow(),
                    size = profileList.getTableView().getListTableModel().getRowCount();
            if (size == 0) {
                if (selectedId != null) {
                    profileForms.get(selectedId).getPanel().setVisible(false);
                    detailLabel.setVisible(true);
                    selectedId = null;
                }
                return;
            }
            if (row >= size || row < 0) {
                row = size - 1;
            }
            int newId = profileList.getTableView().getRow(row);
            if (selectedId != null && selectedId.equals(newId)) {
                return;
            }
            if (profileList.getTableView().getSelection().isEmpty()) {
                profileList.getTableView().setSelection(Collections.singleton(newId));
            }
            ROSProfileForm formToSelect = profileForms.get(newId);
            if (formToSelect == null) {
                formToSelect = new ROSProfileForm(project);
                ret.add(formToSelect.getPanel(), formLayout);
                profileForms.put(newId, formToSelect);
                formToSelect.loadData(Objects.requireNonNull(data.getProfile(newId)), profileList::refresh);
            }
            if (selectedId != null) {
                profileForms.get(selectedId).getPanel().setVisible(false);
            } else {
                detailLabel.setVisible(false);
            }
            selectedId = newId;
            formToSelect.getPanel().setVisible(true);
        });

        existingProfiles = new ArrayList<>(data.loadProfiles());
        profileList.setValues(existingProfiles);

        return ret;
    }

    @Override
    public boolean isModified() {
        return !profileList.getTableView().getItems().equals(existingProfiles)
                || profileForms.values().stream().anyMatch(ROSProfileForm::isModified);
    }

    @Override
    public void apply() {

    }
}
