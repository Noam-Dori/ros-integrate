package ros.integrate.cmake.psi.impl;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ros.integrate.cmake.psi.*;
import ros.integrate.cmake.ref.CMakeCommandReference;
import ros.integrate.cmake.ref.CMakeVariableReference;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public static String getName(@NotNull CMakeCommand cmd) {
        return cmd.getCommandName().getText();
    }

    public static PsiElement setName(@NotNull CMakeCommand cmd, @NotNull String newName) {
        return cmd.getCommandName().replace(CMakeElementFactory.createCommand(cmd.getProject(), newName));
    }

    @NotNull
    public static CMakeCommandName getNameIdentifier(@NotNull CMakeCommand cmd) {
        return cmd.getCommandName();
    }

    public static String getName(@NotNull CMakeUnquotedArgument arg) {
        return arg.getText();
    }

    public static PsiElement setName(@NotNull CMakeUnquotedArgument arg, @NotNull String newName) {
        return arg.replace(CMakeElementFactory.createCommand(arg.getProject(), "set", newName));
    }

    @NotNull
    @Contract("_ -> new")
    public static PsiReference getReference(@NotNull CMakeCommand cmd) {
        return new CMakeCommandReference(cmd);
    }

    @NotNull
    public static CMakeVariableReference[] getReferences(@NotNull CMakeUnquotedArgument arg) {
        return getVarTextRanges(arg.getText(), 0).stream()
                .map(range -> new CMakeVariableReference(arg, range))
                .toArray(CMakeVariableReference[]::new);
    }

    @NotNull
    private static List<TextRange> getVarTextRanges(String text, int offset) {
        Matcher varMatcher = Pattern.compile("\\$(?:ENV)?\\{(.*)}").matcher(text);
        List<TextRange> result = new ArrayList<>();
        while (varMatcher.find()) {
            if (varMatcher.start(1) < varMatcher.end(1)) {
                // there is a found variable
                List<TextRange> insideResult = getVarTextRanges(varMatcher.group(1), offset + varMatcher.start(1));
                if (insideResult.isEmpty()) {
                    // this is the leaf variable level - add a reference!
                    result.add(TextRange.create(varMatcher.start(1), varMatcher.end(1)).shiftRight(offset));
                }
            }
        }
        return result;
    }
}
