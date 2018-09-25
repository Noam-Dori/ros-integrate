package ros.integrate.msg;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.msg.psi.ROSMsgTypes;

/**
 * an extension enabling bracket support in ROS messages.
 */
public class ROSMsgBraceMatcher implements PairedBraceMatcher {
    @NotNull
    @Override
    public BracePair[] getPairs() {
        return new BracePair[]{new BracePair(ROSMsgTypes.LBRACKET,ROSMsgTypes.RBRACKET,false)};
    }

    @Override
    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType) {
        return contextType == ROSMsgTypes.CUSTOM_TYPE || contextType == ROSMsgTypes.KEYTYPE;
    }

    @Override
    public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
        return openingBraceOffset;
    }
}
