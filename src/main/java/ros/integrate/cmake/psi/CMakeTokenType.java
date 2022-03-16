package ros.integrate.cmake.psi;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import ros.integrate.cmake.lang.CMakeLanguage;

/**
 * formally defines ROS packet token types. This is a template for generated token types
 * @author Noam Dori
 */
public class CMakeTokenType extends IElementType {
    public CMakeTokenType(@NotNull String debugName) {
        super(debugName, CMakeLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "CMakeTokenType." + super.toString();
    }
}
