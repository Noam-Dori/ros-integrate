package ros.integrate.pkt;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.psi.ROSPktTypeBase;

// allows manipulations of type PSI elements
public class ROSPktTypeElementManipulator extends AbstractElementManipulator<ROSPktTypeBase> {
    @Nullable
    @Override
    public ROSPktTypeBase handleContentChange(@NotNull ROSPktTypeBase element, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        return (ROSPktTypeBase) element.setName(newContent);
    }
}
