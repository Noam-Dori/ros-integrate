package ros.integrate.cmake.psi.impl;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ros.integrate.cmake.psi.*;

import java.util.List;

public class CMakePsiImplUtil {
    @NotNull
    public static List<CMakeArgument> getArguments(@NotNull CMakeCommand cmd) {
        return PsiTreeUtil.getChildrenOfAnyType(cmd.getArgumentList(), CMakeArgument.class);
    }

    public static TextRange getArgTextRange(@NotNull CMakeUnquotedArgument arg) {
        return arg.getTextRange();
    }

    @NotNull
    @Contract("_ -> new")
    public static TextRange getArgTextRange(@NotNull CMakeQuotedArgument arg) {
        PsiElement[] elements = arg.getChildren();
        int start = elements[1].getTextOffset();
        int end = elements[elements.length - 2].getTextRange().getEndOffset();
        return new TextRange(start, end);
    }

    @NotNull
    @Contract("_ -> new")
    public static TextRange getArgTextRange(@NotNull CMakeBracketArgument arg) {
        PsiElement[] elements = arg.getChildren();
        int start = elements[1].getTextOffset();
        int end = elements[elements.length - 2].getTextRange().getEndOffset();
        return new TextRange(start, end);
    }
}
