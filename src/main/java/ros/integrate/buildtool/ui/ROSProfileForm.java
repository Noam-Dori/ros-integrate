package ros.integrate.buildtool.ui;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import ros.integrate.buildtool.ROSBuildTool;
import ros.integrate.buildtool.ROSProfile;

import javax.swing.*;
import javax.swing.event.DocumentEvent;

/**
 * the GUI component that allows editing details about a specific profile while remaining
 * detached from the data component (until the user specifically requests so)
 * @author Noam Dori
 */
public class ROSProfileForm {
    @NotNull
    private final JPanel panel;

    private final JBTextField name = new JBTextField();
    private final ComboBox<ROSBuildTool> buildtool = new ComboBox<>(ROSBuildTool.values());

    public ROSProfileForm() {
        JBLabel nameLabel = new JBLabel("Name:");
        JBLabel buildtoolLabel = new JBLabel("Build Tool:");

        buildtool.setEditable(false);
        buildtool.setRenderer(new DefaultListCellRenderer() {
            @Override
            public JLabel getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel ret = (JLabel) super.getListCellRendererComponent(list, value.toString().toLowerCase(),
                        index, isSelected, cellHasFocus);
                ret.setIcon(ROSBuildTool.valueOf(value.toString()).getIcon());
                return ret;
            }
        });

        panel = new FormBuilder()
                .addLabeledComponent(nameLabel, name)
                .addComponent(new JSeparator())
                .addLabeledComponent(buildtoolLabel, buildtool)
                .getPanel();
    }

    @NotNull
    public JPanel getPanel() {
        return panel;
    }

    public void loadData(@NotNull ROSProfile profile, Runnable update) {
        name.setText(profile.getName());
        buildtool.setItem(profile.getBuildtool());

        name.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                profile.setGuiName(name.getText());
                update.run();
            }
        });

        buildtool.addItemListener(event -> {
            profile.setGuiBuildtool(buildtool.getItem());
            update.run();
        });
    }
}
