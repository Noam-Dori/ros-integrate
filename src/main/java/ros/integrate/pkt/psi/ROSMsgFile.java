package ros.integrate.pkt.psi;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.file.ROSMsgFileType;

/**
 * represents a message, which describes the medium passed from one node to another.
 * This medium consists of data that is basically like a C/C++ struct: a bunch of fields of various types.
 * Messages are one-directional and act as the base unit for the ROS middleware unit
 * Files of this type use the .msg extension
 * @author Noam Dori
 */
public class ROSMsgFile extends ROSPktFile {
    /**
     * construct a new message file
     * @param viewProvider the file view provider. go figure
     */
    public ROSMsgFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return ROSMsgFileType.INSTANCE;
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return null;
    }

    @Override
    String getDotDefaultExtension() {
        return ROSMsgFileType.DOT_DEFAULT_EXTENSION;
    }

    @Override
    public int getMaxSeparators() {
        return 0;
    }

    @Override
    public String getTooManySeparatorsMessage() {
        return "ROS Messages cannot have separators";
    }
}
