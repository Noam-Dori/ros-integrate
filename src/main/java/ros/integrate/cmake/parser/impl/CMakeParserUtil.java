package ros.integrate.cmake.parser.impl;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static ros.integrate.cmake.parser.CMakeParser.command;

public class CMakeParserUtil extends GeneratedParserUtilBase {
    public static boolean namedCommand(@NotNull PsiBuilder builder, int level, @NotNull String searchName) {
        return searchName.equals(builder.getTokenText()) && command(builder, level);
    }

    public static boolean unnamedCommand(@NotNull PsiBuilder builder, int level, @NotNull String... searchName) {
        return !Arrays.asList(searchName).contains(builder.getTokenText()) && command(builder, level);
    }
}
