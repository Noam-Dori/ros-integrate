package ros.integrate.msg.psi;

import com.intellij.psi.tree.IElementType;
import ros.integrate.msg.ROSMsgLanguage;
import org.jetbrains.annotations.NotNull;

public class ROSMsgTokenType extends IElementType {
    public ROSMsgTokenType(@NotNull String debugName) {
        super(debugName, ROSMsgLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "ROSMsgTokenType" + super.toString();
    }
}