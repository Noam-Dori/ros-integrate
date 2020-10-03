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
 * formal definition of .msg files. It gives these files their own icon
 * @author Noam Dori
 */
public class ROSMsgFileType extends ROSPktFileType {
    public static final ROSPktFileType INSTANCE = new ROSMsgFileType();
    @NonNls private static final String DEFAULT_EXTENSION = "msg";
    @NonNls public static final String DOT_DEFAULT_EXTENSION = "." + DEFAULT_EXTENSION;

    /**
     * construct an instance of this definition
     */
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
        return "Message";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return ROSIcons.MSG_FILE;
    }
}