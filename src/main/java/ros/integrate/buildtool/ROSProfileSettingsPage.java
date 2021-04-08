package ros.integrate.buildtool;

import com.intellij.openapi.options.SearchableConfigurable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * the user interface that allows the user to add,get, or modify the project's ROS buildtool configurations,
 * also called profiles.
 * @author Noam Dori
 */
public class ROSProfileSettingsPage implements SearchableConfigurable {
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
        return null;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() {

    }
}
