package ros.integrate.cmake.ref;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.Nullable;
import ros.integrate.cmake.CMakeClasses;
import ros.integrate.cmake.CMakeUtil;

import java.util.ArrayList;
import java.util.Arrays;

public class CMakeVariableReference extends PsiReferenceBase<PsiElement> {
    public CMakeVariableReference(PsiElement element, TextRange range) {
        super(element, range, true);
    }

    @Override
    public @Nullable PsiElement resolve() {
        String varName = myElement.getText().substring(getRangeInElement().getStartOffset(), getRangeInElement().getEndOffset());
        PsiElement result = CMakeUtil.findCommands(myElement.getContainingFile(), "set")
                .stream().map(cmd -> CMakeUtil.deepSearch(cmd, CMakeClasses.getArgClass(), 2).get(0))
                .filter(arg -> arg.getText().equals(varName))
                .findFirst().orElse(null);
        if (result == null && Arrays.asList("function", "macro").contains(myElement.getParent().getParent().getParent()
                .getFirstChild().getFirstChild().getText())) {
            result = CMakeUtil.findCommands(myElement.getParent().getParent().getParent(), "function", "macro")
                    .stream().map(cmd -> CMakeUtil.deepSearch(cmd, CMakeClasses.getArgClass(), 2))
                    .peek(list -> list.remove(0))
                    .findFirst().orElse(new ArrayList<>()).stream()
                    .filter(arg -> arg.getText().equals(varName))
                    .findFirst().orElse(null);
        }
        return result;
    }
}
