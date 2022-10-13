package ros.integrate.cmake.parser.impl;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

import static ros.integrate.cmake.parser.CMakeParser.*;
import static ros.integrate.cmake.psi.CMakeTypes.*;

public class CMakeParserUtil extends GeneratedParserUtilBase {
    public static boolean namedCommand(@NotNull PsiBuilder builder, int level, @NotNull String searchName) {
        return searchName.equals(builder.getTokenText()) && command(builder, level);
    }

    public static boolean unnamedCommand(@NotNull PsiBuilder builder, int level, @NotNull String... searchName) {
        return !Arrays.asList(searchName).contains(builder.getTokenText()) && command(builder, level);
    }

    // no whitespace && (TEXT_ELEMENT | ESCAPE_SEQUENCE | (PAREN_OPEN argument_element? PAREN_CLOSE?))+
    public static boolean unquotedElement(@NotNull PsiBuilder b, int l, BiFunction<PsiBuilder, Integer, Boolean> rule) {
        if (!recursion_guard_(b, l, "unquotedElement")) return false;
        boolean r;
        PsiBuilder.Marker m = enter_section_(b);
        r = unquotedPiece(b, l + 1, rule);
        if (r)
            while (" \t".indexOf(b.getOriginalText().charAt(b.getCurrentOffset() - 1)) < 0) {
                int c = current_position_(b);
                if (!unquotedPiece(b, l + 1, rule)) break;
                if (!empty_element_parsed_guard_(b, "unquotedElement", c)) break;
            }
        exit_section_(b, m, null, r);
        return r;
    }

    private static boolean unquotedPiece(@NotNull PsiBuilder b, int l, BiFunction<PsiBuilder, Integer, Boolean> rule) {
        if (!recursion_guard_(b, l, "unquotedPiece")) return false;
        boolean r;
        PsiBuilder.Marker m = enter_section_(b);
        r = consumeToken(b, TEXT_ELEMENT);
        if (!r) r = consumeToken(b, ESCAPE_SEQUENCE);
        if (!r) r = rule.apply(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }
}
