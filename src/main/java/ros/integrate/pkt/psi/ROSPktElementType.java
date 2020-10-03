package ros.integrate.pkt.psi;

import com.intellij.psi.tree.IElementType;
import ros.integrate.pkt.lang.ROSPktLanguage;
import org.jetbrains.annotations.NotNull;

/**
 * formally defines ROS packet element types. This is a template for generated token types.
 * What is the difference between this and token types? no idea
 * @author Noam Dori
 */
public class ROSPktElementType extends IElementType {
    ROSPktElementType(@NotNull String debugName) {
        super(debugName, ROSPktLanguage.INSTANCE);
    }
}
