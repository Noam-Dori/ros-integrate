package ros.integrate.msg.psi;

import com.intellij.psi.tree.IElementType;
import ros.integrate.msg.ROSPktLanguage;
import org.jetbrains.annotations.NotNull;

/**
 * generic representation of a token type in ros messages.
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