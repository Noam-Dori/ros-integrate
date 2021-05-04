package ros.integrate.buildtool.ui;

import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
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

    public ROSProfileForm() {
        JBLabel nameLabel = new JBLabel("Name:");

        panel = new FormBuilder()
                .addLabeledComponent(nameLabel, name)
                .addComponent(new JSeparator())
                .getPanel();
    }

    @NotNull
    public JPanel getPanel() {
        return panel;
    }

    public void loadData(@NotNull ROSProfile profile, Runnable update) {
        name.setText(profile.getName());

        name.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                profile.setGuiName(name.getText());
                update.run();
            }
        });
    }
}
