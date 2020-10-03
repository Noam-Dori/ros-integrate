package ros.integrate.pkt.psi;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.file.ROSSrvFileType;

/**
 * represents a service, which describes the medium passed between two nodes when one requests another to
 * complete a short term task. This has two sections:
 * <ol>
 *     <li>the request: the client tells the server details about the task it needs to do</li>
 *     <li>the response: the server completes the task and sends back results</li>
 * </ol>
 * Files of this type use the .srv extension
 * @author Noam Dori
 */
public class ROSSrvFile extends ROSPktFile {
    /**
     * construct a new service file
     * @param viewProvider the file view provider. go figure
     */
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
