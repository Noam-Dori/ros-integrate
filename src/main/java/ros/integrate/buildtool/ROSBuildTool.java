package ros.integrate.buildtool;

import org.jetbrains.annotations.NotNull;
import ros.integrate.ROSIcons;

import javax.swing.*;

/**
 * A list of available ROS buildtools, that is,
 * the top level program that triggers and manages the build process.
 * This includes the CMake step, the compiler, etc.
 *
 * @author Noam Dori
 */
public enum ROSBuildTool {
    /**
     * <a href="http://wiki.ros.org/catkin/commands/catkin_make">catkin_make</a>, or catkin,
     * is the second generation tool created by ROS to build packages.
     * This is the tool taught in the ROS tutorials.
     * This is the only tool that builds without isolation.
     */
    CATKIN_MAKE(ROSIcons.CATKIN_MAKE),
    /**
     * <a href="https://catkin-tools.readthedocs.io/en/latest/">catkin_tools</a>
     * is an expansion of catkin_make, which allows a more customizable build process,
     * from blacklist/whitelist, to saving various arguments.
     * While this is built on top of catkin, it does not offer merged building, only build in isolation.
     */
    CATKIN_TOOLS(ROSIcons.CATKIN),
    /**
     * <a href="https://colcon.readthedocs.io/en/released/">colcon</a>
     * is the third generation tool created by ROS to build packages.
     * It is the only tool capable of building ROS2 packages, but also supports ROS1 packages.
     * colcon offers features very similar to those offered by catkin_tools.
     * This is the buildtool currently endorsed by ROS and Open Robotics.
     */
    COLCON(ROSIcons.COLCON);

    @NotNull
    private final Icon icon;

    /**
     * create a new buildtool indicator
     * @param icon the icon representing the buildtool
     */
    ROSBuildTool(@NotNull Icon icon) {
        this.icon = icon;
    }

    /**
     * @return the icon that represents this buildtool
     */
    @NotNull
    public Icon getIcon() {
        return icon;
    }
}
