package ros.integrate.msg;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.msg.psi.ROSMsgType;

public class ROSMsgTypeElementManipulator extends AbstractElementManipulator<ROSMsgType> {
    @Nullable
    @Override
    public ROSMsgType handleContentChange(@NotNull ROSMsgType element, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        return (ROSMsgType) element.setName(newContent);
    }
}
