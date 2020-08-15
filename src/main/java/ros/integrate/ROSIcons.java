package ros.integrate;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * a map containing information for a bunch of usable icons in the ROS plugin
 * A useful list of icons: https://jetbrains.design/intellij/resources/icons_list/
 */
public class ROSIcons {
    public static final Icon MSG_FILE = IconLoader.getIcon("/icons/msgFile.png");
    public static final Icon SRV_FILE = IconLoader.getIcon("/icons/srvFile.png");
    public static final Icon ACT_FILE = IconLoader.getIcon("/icons/actionFile.png");
    public static final Icon SRC_PKG = IconLoader.getIcon("/icons/srcPackage.svg");
    public static final Icon LIB_PKG = IconLoader.getIcon("/icons/libPackage.svg");
    public static final Icon DEP_KEY = IconLoader.getIcon("/icons/rosdepKey.svg");
    public static final Icon CONDITION = IconLoader.getIcon("/icons/condition.svg");
    public static final Icon GROUP = AllIcons.Nodes.Tag;
}
