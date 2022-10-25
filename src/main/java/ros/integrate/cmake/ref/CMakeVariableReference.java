package ros.integrate.cmake.ref;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.cmake.CMakeClasses;
import ros.integrate.cmake.psi.CMakeFunction;

public class CMakeVariableReference extends PsiReferenceBase<PsiElement> {
    public CMakeVariableReference(PsiElement element, TextRange range) {
        super(element, range, true); // TODO implement.
    }

    @Override
    public @Nullable PsiElement resolve() {
        return null;
//        PsiTreeUtil.getChildrenOfAnyType(myElement.getContainingFile(), CMakeFunction)
    }
}
