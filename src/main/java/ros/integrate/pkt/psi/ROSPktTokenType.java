package ros.integrate.pkt.psi;

import com.intellij.psi.tree.IElementType;
import ros.integrate.pkt.lang.ROSPktLanguage;
import org.jetbrains.annotations.NotNull;

/**
 * formally defines ROS packet token types. This is a template for generated token types
 * @author Noam Dori
 */
public class ROSPktTokenType extends IElementType {
    ROSPktTokenType(@NotNull String debugName) {
        super(debugName, ROSPktLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "ROSMsgTokenType" + super.toString();
    }
}