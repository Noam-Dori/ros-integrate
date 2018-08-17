package ros.integrate.msg.psi;

import com.intellij.psi.tree.IElementType;
import ros.integrate.msg.ROSMsgLanguage;
import org.jetbrains.annotations.NotNull;

public class ROSMsgElementType extends IElementType {
    public ROSMsgElementType(@NotNull String debugName) {
        super(debugName, ROSMsgLanguage.INSTANCE);
    }
}
