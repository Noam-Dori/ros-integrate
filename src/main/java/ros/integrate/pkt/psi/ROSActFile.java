package ros.integrate.pkt.psi;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.file.ROSActFileType;

/**
 * a ROS message, a one-directional message sent between (and within) executables.
 */
public class ROSActFile extends ROSPktFile {
    public ROSActFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return ROSActFileType.INSTANCE;
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return null;
    }

    @Override
    String getDotDefaultExtension() {
        return ROSActFileType.DOT_DEFAULT_EXTENSION;
    }

    @Override
    public int getMaxSeparators() {
        return 2;
    }

    @Override
    public String getTooManySeparatorsMessage() {
        return "ROS Actions can only have two separators";
    }
}
