package ros.integrate.pkt.file;

import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.ROSIcons;
import ros.integrate.pkt.psi.ROSPktFile;
import ros.integrate.pkt.psi.ROSSrvFile;

import javax.swing.*;

/**
 * @author Noam Dori
 * a class defining the ROS Msg file type (and perhaps the service one as well)
 */
public class ROSSrvFileType extends ROSPktFileType {
    public static final ROSPktFileType INSTANCE = new ROSSrvFileType();
    @NonNls private static final String DEFAULT_EXTENSION = "srv";
    @NonNls public static final String DOT_DEFAULT_EXTENSION = "." + DEFAULT_EXTENSION;

    private ROSSrvFileType() {
        super();
    }

    @Override
    public ROSPktFile newPktFile(FileViewProvider viewProvider) {
        return new ROSSrvFile(viewProvider);
    }

    @NotNull
    @Override
    public String getName() {
        return "ROSSrv";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Service";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return ROSIcons.SrvFile;
    }
}