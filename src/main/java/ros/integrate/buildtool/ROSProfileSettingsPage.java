package ros.integrate.buildtool;

import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.buildtool.ui.SelectableListTable;

import javax.swing.*;
import java.awt.*;

/**
 * the user interface that allows the user to add,get, or modify the project's ROS buildtool configurations,
 * also called profiles.
 * @author Noam Dori
 */
public class ROSProfileSettingsPage implements SearchableConfigurable {
    private final SelectableListTable profileList = new SelectableListTable();

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
        listLayout.anchor = GridBagConstraints.FIRST_LINE_START;
        ret.add(profileList.getComponent(), listLayout);

        GridBagConstraints formLayout = new GridBagConstraints();
        formLayout.weightx = formLayout.weighty = formLayout.gridx = 1;
        formLayout.gridy = 0;
        formLayout.fill = GridBagConstraints.HORIZONTAL;
        formLayout.anchor = GridBagConstraints.NORTHWEST;
        formLayout.insets.left = 10;
        formLayout.insets.top = 5;
        ret.add(new FormBuilder()
                .addLabeledComponent(new JBLabel("Name:"), new JBTextField())
                .addComponent(new JSeparator())
                .getPanel(), formLayout);


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
