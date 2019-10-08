package ros.integrate.pkt.file;

import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.ROSIcons;
import ros.integrate.pkt.psi.ROSPktFile;
import ros.integrate.pkt.psi.ROSActFile;

import javax.swing.*;

/**
 * @author Noam Dori
 * a class defining the ROS Msg file type (and perhaps the service one as well)
 */
public class ROSActFileType extends ROSPktFileType {
    public static final ROSPktFileType INSTANCE = new ROSActFileType();
    @NonNls private static final String DEFAULT_EXTENSION = "action";
    @NonNls public static final String DOT_DEFAULT_EXTENSION = "." + DEFAULT_EXTENSION;

    private ROSActFileType() {
        super();
    }

    @Override
    public ROSPktFile newPktFile(FileViewProvider viewProvider) {
        return new ROSActFile(viewProvider);
    }

    @NotNull
    @Override
    public String getName() {
        return "ROSAct";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Action";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return ROSIcons.ActFile;
    }
}