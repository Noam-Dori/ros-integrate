package ros.integrate.pkt.psi;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.file.ROSSrvFileType;

/**
 * a ROS message, a one-directional message sent between (and within) executables.
 */
public class ROSSrvFile extends ROSPktFile {
    public ROSSrvFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return ROSSrvFileType.INSTANCE;
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return null;
    }

    @Override
    String getDotDefaultExtension() {
        return ROSSrvFileType.DOT_DEFAULT_EXTENSION;
    }

    @Override
    public int getMaxSeparators() {
        return 1;
    }

    @Override
    public String getTooManySeparatorsMessage() {
        return "ROS Services can only have one separator";
    }
}
