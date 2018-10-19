package ros.integrate.msg.psi;

import com.intellij.psi.tree.IElementType;
import ros.integrate.msg.ROSPktLanguage;
import org.jetbrains.annotations.NotNull;

/**
 * generic representation of an element type in ros messages.
 */
public class ROSPktElementType extends IElementType {
    ROSPktElementType(@NotNull String debugName) {
        super(debugName, ROSPktLanguage.INSTANCE);
    }
}
