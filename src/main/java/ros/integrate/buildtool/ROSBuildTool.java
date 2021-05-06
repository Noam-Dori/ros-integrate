package ros.integrate.buildtool;

import org.jetbrains.annotations.NotNull;
import ros.integrate.ROSIcons;

import javax.swing.*;

public enum ROSBuildTool {
    CATKIN_MAKE(ROSIcons.CATKIN_MAKE),
    CATKIN_TOOLS(ROSIcons.CATKIN),
    COLCON(ROSIcons.COLCON);

    @NotNull
    private final Icon icon;

    ROSBuildTool(@NotNull Icon icon) {
        this.icon = icon;
    }

    @NotNull
    public Icon getIcon() {
        return icon;
    }
}
