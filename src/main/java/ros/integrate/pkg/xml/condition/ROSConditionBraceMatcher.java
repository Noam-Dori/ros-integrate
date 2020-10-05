package ros.integrate.pkg.xml.condition;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.xml.condition.psi.ROSConditionTypes;

/**
 * implements automatic parentheses matching in ROS conditions
 * @author Noam Dori
 */
public class ROSConditionBraceMatcher implements PairedBraceMatcher {
    @NotNull
    @Override
    public BracePair[] getPairs() {
        return new BracePair[]{new BracePair(ROSConditionTypes.LPARENTHESIS, ROSConditionTypes.RPARENTHESIS, true)};
    }

    @Override
    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType braceType, @Nullable IElementType contextType) {
        return true;
    }

    @Override
    public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
        return openingBraceOffset;
    }
}
