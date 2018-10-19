package ros.integrate.pkt.psi;

import com.intellij.psi.tree.IElementType;
import ros.integrate.pkt.lang.ROSPktLanguage;
import org.jetbrains.annotations.NotNull;

/**
 * generic representation of an element type in ros messages.
 */
public class ROSPktElementType extends IElementType {
    ROSPktElementType(@NotNull String debugName) {
        super(debugName, ROSPktLanguage.INSTANCE);
    }
}
