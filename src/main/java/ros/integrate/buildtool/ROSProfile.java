package ros.integrate.buildtool;

import org.jetbrains.annotations.NotNull;
import ros.integrate.ROSIcons;

import javax.swing.*;

// TODO: 5/4/2021 implement away the mock stuff
public class ROSProfile {
    @NotNull
    public String getName() {
        return "mock name";
    }

    @NotNull
    public Icon getIcon() {
        return ROSIcons.CATKIN_MAKE;
    }
}
