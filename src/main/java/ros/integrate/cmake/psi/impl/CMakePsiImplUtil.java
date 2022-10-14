package ros.integrate.cmake.psi.impl;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
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
        StringBuilder builder = new StringBuilder();
        for (PsiElement i = arg.getFirstChild().getNextSibling(); !i.equals(arg.getLastChild()); i = i.getNextSibling()) {
            builder.append(i.getText());
        }
        return builder.toString();
    }

    @NotNull
    public static String getName(@NotNull CMakeCommandName cmdName) {
        return cmdName.getText();
    }

    public static PsiElement setName(@NotNull CMakeCommandName cmdName, @NotNull String newName) {
        return cmdName.replace(CMakeElementFactory.createCommand(cmdName.getProject(), newName));
    }

    @NotNull
    public static String getName(@NotNull CMakeCommand cmd) {
        return cmd.getCommandName().getText();
    }

    public static PsiElement setName(@NotNull CMakeCommand cmd, @NotNull String newName) {
        return cmd.getCommandName().setName(newName);
    }

    @NotNull
    public static CMakeCommandName getNameIdentifier(@NotNull CMakeCommand cmd) {
        return cmd.getCommandName();
    }
}
