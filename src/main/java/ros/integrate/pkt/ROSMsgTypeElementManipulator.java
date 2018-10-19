package ros.integrate.pkt;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.psi.ROSPktType;

//unused for now
public class ROSMsgTypeElementManipulator extends AbstractElementManipulator<ROSPktType> {
    @Nullable
    @Override
    public ROSPktType handleContentChange(@NotNull ROSPktType element, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        return (ROSPktType) element.setName(newContent);
    }
}
