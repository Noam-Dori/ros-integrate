package ros.integrate.pkt;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.psi.ROSPktTypes;

/**
 * implements automatic bracket matching at the end of field types in packet files (.msg, .srv, .action)
 * @author Noam Dori
 */
public class ROSPktBraceMatcher implements PairedBraceMatcher {
    @NotNull
    @Override
    public BracePair[] getPairs() {
        return new BracePair[]{new BracePair(ROSPktTypes.LBRACKET, ROSPktTypes.RBRACKET,false)};
    }

    @Override
    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType braceType, @Nullable IElementType contextType) {
        return contextType == ROSPktTypes.CUSTOM_TYPE || contextType == ROSPktTypes.KEYTYPE;
    }

    @Override
    public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
        return openingBraceOffset;
    }
}
