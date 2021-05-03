package ros.integrate;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * a map containing information for a bunch of usable icons in the ROS plugin
 * A useful list of icons: https://jetbrains.design/intellij/resources/icons_list/
 * @author Noam Dori
 */
public interface ROSIcons {
    /** 16x16 */ Icon MSG_FILE = IconLoader.getIcon("/icons/msgFile.png");
    /** 16x16 */ Icon SRV_FILE = IconLoader.getIcon("/icons/srvFile.png");
    /** 16x16 */ Icon ACT_FILE = IconLoader.getIcon("/icons/actionFile.png");
    /** 16x16 */ Icon SRC_PKG = IconLoader.getIcon("/icons/srcPackage.svg");
    /** 16x16 */ Icon LIB_PKG = IconLoader.getIcon("/icons/libPackage.svg");
    /** 16x16 */ Icon DEP_KEY = IconLoader.getIcon("/icons/rosdepKey.svg");
    /** 16x16 */ Icon CONDITION = IconLoader.getIcon("/icons/condition.svg");
    /** 13x13 */ Icon CATKIN = IconLoader.getIcon("/icons/catkin.svg");
    /** 13x13 */ Icon CATKIN_MAKE = IconLoader.getIcon("/icons/catkin_make.svg");
    /** 13x12 */ Icon COLCON = IconLoader.getIcon("/icons/colcon.svg");
    /** 16x16 */ Icon GROUP = AllIcons.Nodes.Tag;
}
