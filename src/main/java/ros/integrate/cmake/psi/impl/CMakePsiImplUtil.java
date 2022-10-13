package ros.integrate.cmake.psi.impl;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ros.integrate.cmake.psi.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        return textRange(arg);
    }

    @NotNull
    @Contract("_ -> new")
    public static TextRange getArgTextRange(@NotNull CMakeBracketArgument arg) {
        return textRange(arg);
    }

    @NotNull
    @Contract("_ -> new")
    private static TextRange textRange(@NotNull CMakeArgument arg) {
        PsiElement[] elements = arg.getChildren();
        int start = elements[1].getTextOffset();
        int end = elements[elements.length - 2].getTextRange().getEndOffset();
        return new TextRange(start, end);
    }

    @NotNull
    public static String getArgText(@NotNull CMakeUnquotedArgument arg) {
        return arg.getText();
    }

    @NotNull
    public static String getArgText(@NotNull CMakeQuotedArgument arg) {
        return argText(arg);
    }

    @NotNull
    public static String getArgText(@NotNull CMakeBracketArgument arg) {
        return argText(arg);
    }

    @NotNull
    public static String argText(@NotNull CMakeArgument arg) {
        List<PsiElement> elements = new ArrayList<>(Arrays.asList(arg.getChildren()));
        elements.remove(0);
        elements.remove(elements.size() - 1);
        return elements.stream().map(PsiElement::getText).collect(Collectors.joining());
    }
}
