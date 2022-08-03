package ros.integrate.cmake.lang;

import com.intellij.lang.Commenter;
import org.jetbrains.annotations.Nullable;

/**
 * The very basic commenter for CMake comments. This is not ware of the variable length block comments, like
 * [=[comment]=]
 * It does do its job good enough though.
 * @author Noam Dori
 */
public class CMakeCommenter implements Commenter {
    @Override
    public @Nullable String getLineCommentPrefix() {
        return "#";
    }

    @Override
    public @Nullable String getBlockCommentPrefix() {
        return "#[[";
    }

    @Override
    public @Nullable String getBlockCommentSuffix() {
        return "]]";
    }

    @Override
    public @Nullable String getCommentedBlockCommentPrefix() {
        return null;
    }

    @Override
    public @Nullable String getCommentedBlockCommentSuffix() {
        return null;
    }
}
