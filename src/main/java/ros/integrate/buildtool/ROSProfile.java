package ros.integrate.buildtool;

import org.jetbrains.annotations.NotNull;
import ros.integrate.ROSIcons;

import javax.swing.*;

/**
 * stores information about one ROS build profile, independent of the buildtool.
 * @author Noam Dori
 */
// TODO: 5/4/2021 implement away the mock stuff
public class ROSProfile {
    private String name = "mock name";
    private String guiName = name;

    @NotNull
    public String getName() {
        return name;
    }

    String getGuiName() {
        return guiName;
    }

    @NotNull
    public Icon getIcon() {
        return ROSIcons.CATKIN_MAKE;
    }

    public void setGuiName(String newName) {
        guiName = newName;
    }

    public void setName(String newName) {
        name = newName;
    }

    public void saveName() {
        name = guiName;
    }
}
