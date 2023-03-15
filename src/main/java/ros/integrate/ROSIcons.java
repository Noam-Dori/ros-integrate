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
    /** 16x16 */ Icon MSG_FILE = IconLoader.getIcon("/icons/msgFile.png", ROSIcons.class.getClassLoader());
    /** 16x16 */ Icon SRV_FILE = IconLoader.getIcon("/icons/srvFile.png", ROSIcons.class.getClassLoader());
    /** 16x16 */ Icon ACT_FILE = IconLoader.getIcon("/icons/actionFile.png", ROSIcons.class.getClassLoader());
    /** 16x16 */ Icon SRC_PKG = IconLoader.getIcon("/icons/srcPackage.svg", ROSIcons.class.getClassLoader());
    /** 16x16 */ Icon LIB_PKG = IconLoader.getIcon("/icons/libPackage.svg", ROSIcons.class.getClassLoader());
    /** 16x16 */ Icon DEP_KEY = IconLoader.getIcon("/icons/rosdepKey.svg", ROSIcons.class.getClassLoader());
    /** 16x16 */ Icon CONDITION = IconLoader.getIcon("/icons/condition.svg", ROSIcons.class.getClassLoader());
    /** 13x13 */ Icon CATKIN = IconLoader.getIcon("/icons/catkin.svg", ROSIcons.class.getClassLoader());
    /** 13x13 */ Icon CATKIN_MAKE = IconLoader.getIcon("/icons/catkin_make.svg", ROSIcons.class.getClassLoader());
    /** 13x12 */ Icon COLCON = IconLoader.getIcon("/icons/colcon.svg", ROSIcons.class.getClassLoader());
    /** 16x16 */ Icon GROUP = AllIcons.Nodes.Tag;
    /** 16x16 */ Icon LIST_FILES = IconLoader.getIcon("/icons/listFiles.svg", ROSIcons.class.getClassLoader());
    /** 16x16 */ Icon LIST_FILES_HOVER = IconLoader.getIcon("/icons/listFilesHover.svg", ROSIcons.class.getClassLoader());
    /** 16x16 */ Icon CMAKE_FILE = IconLoader.getIcon("/icons/cmake.svg", ROSIcons.class.getClassLoader());
}
