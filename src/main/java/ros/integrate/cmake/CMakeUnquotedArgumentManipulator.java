package ros.integrate.cmake;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.cmake.adapter.CMakeArgumentAdapter;

public class CMakeUnquotedArgumentManipulator extends AbstractElementManipulator<PsiElement> {
    @Override
    public @Nullable PsiElement handleContentChange(@NotNull PsiElement element, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        if (CMakeClasses.getUnquotedArgClass().isInstance(element)) {
            new CMakeArgumentAdapter(element).setText(newContent);
            return element;
        }
        return null;
    }
}
