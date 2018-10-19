package ros.integrate.pkt.file;

import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.ROSIcons;
import ros.integrate.pkt.psi.ROSMsgFile;
import ros.integrate.pkt.psi.ROSPktFile;

import javax.swing.*;

/**
 * @author Noam Dori
 * a class defining the ROS Msg file type (and perhaps the service one as well)
 */
public class ROSMsgFileType extends ROSPktFileType {
    public static final ROSPktFileType INSTANCE = new ROSMsgFileType();
    @NonNls private static final String DEFAULT_EXTENSION = "pkt";
    @NonNls public static final String DOT_DEFAULT_EXTENSION = "." + DEFAULT_EXTENSION;

    private ROSMsgFileType() {
        super();
    }

    @Override
    public ROSPktFile newPktFile(FileViewProvider viewProvider) {
        return new ROSMsgFile(viewProvider);
    }

    @NotNull
    @Override
    public String getName() {
        return "ROSMsg";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "ROS Message File";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return ROSIcons.MsgFile;
    }
}