package ros.integrate.cmake;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.cmake.psi.CMakeCommand;
import ros.integrate.cmake.psi.CMakeTypes;

import java.util.Arrays;

public class CMakeBraceMatcher implements PairedBraceMatcher {
    @Override
    @NotNull
    public BracePair[] getPairs() {
        return new BracePair[]{new BracePair(CMakeTypes.PAREN_OPEN, CMakeTypes.PAREN_CLOSE, true)};
    }

    @Override
    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType) {
        return Arrays.asList(CMakeTypes.PAREN_OPEN, CMakeTypes.PAREN_CLOSE).contains(contextType);
    }

    @Override
    public int getCodeConstructStart(@NotNull PsiFile file, int openingBraceOffset) {
        PsiElement found = file.findElementAt(openingBraceOffset);
        if (found == null) {
            return openingBraceOffset;
        }
        PsiElement parent = found.getParent();
        if (!(parent instanceof CMakeCommand)) {
            return openingBraceOffset;
        }
        return parent.getTextOffset();
    }
}
