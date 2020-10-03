package ros.integrate.pkt.psi;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.file.ROSActFileType;

/**
 * represents an action, which describes the medium passed between two nodes when one requests another to
 * complete a long term task. This has three sections:
 * <ol>
 *     <li>the request: the client tells the server details about the task it needs to do</li>
 *     <li>feedback: the server continuously updates the client on the progress it makes</li>
 *     <li>the response: the server completes the task and sends back results</li>
 * </ol>
 * Files of this type use the .action extension
 * @author Noam Dori
 */
public class ROSActFile extends ROSPktFile {
    /**
     * construct a new action file
     * @param viewProvider the file view provider. go figure
     */
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
