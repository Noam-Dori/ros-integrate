package ros.integrate.pkt;

import com.intellij.lang.Commenter;
import org.jetbrains.annotations.Nullable;

/**
 * implements the Ctrl+/ action that automatically converts a code line to a comment
 * in packet files (.msg, .srv, .action)
 * @author Noam Dori
 */
public class ROSPktCommenter implements Commenter {
    @Nullable
    @Override
    public String getLineCommentPrefix() {
        return "#";
    }

    @Nullable
    @Override
    public String getBlockCommentPrefix() {
        return null;
    }

    @Nullable
    @Override
    public String getBlockCommentSuffix() {
        return null;
    }

    @Nullable
    @Override
    public String getCommentedBlockCommentPrefix() {
        return null;
    }

    @Nullable
    @Override
    public String getCommentedBlockCommentSuffix() {
        return null;
    }
}
