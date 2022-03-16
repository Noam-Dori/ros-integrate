package ros.integrate.cmake.highlight;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import ros.integrate.cmake.lang.CMakeLexerAdapter;
import ros.integrate.cmake.psi.CMakeTypes;

import java.util.Arrays;

import static com.intellij.openapi.editor.colors.TextAttributesKey.EMPTY_ARRAY;
import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class CMakeSyntaxHighlighter extends SyntaxHighlighterBase {
    public static final TextAttributesKey PARENTHESES =
            createTextAttributesKey("CMAKE_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES);
    public static final TextAttributesKey BRACKETS =
            createTextAttributesKey("CMAKE_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS);
    public static final TextAttributesKey STRING =
            createTextAttributesKey("CMAKE_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey ESCAPE =
            createTextAttributesKey("CMAKE_ESCAPE", DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE);
    public static final TextAttributesKey QUOTE_STRING =
            createTextAttributesKey("CMAKE_QUOTE_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey BRACKET_STRING =
            createTextAttributesKey("CMAKE_BRACKET_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey COMMAND_DECLARATION =
            createTextAttributesKey("CMAKE_COMMAND_DECLARATION", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);
    public static final TextAttributesKey COMMAND_DEFINITION =
            createTextAttributesKey("CMAKE_COMMAND_CALL", DefaultLanguageHighlighterColors.FUNCTION_CALL);
    public static final TextAttributesKey VARIABLE =
            createTextAttributesKey("CMAKE_VARIABLE", DefaultLanguageHighlighterColors.LOCAL_VARIABLE);
    public static final TextAttributesKey VARIABLE_CALL =
            createTextAttributesKey("CMAKE_VARIABLE_CALL", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey KEYWORD =
            createTextAttributesKey("CMAKE_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey ARGUMENT_NAME =
            createTextAttributesKey("CMAKE_ARGUMENT_NAME", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey LINE_COMMENT =
            createTextAttributesKey("CMAKE_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey BLOCK_COMMENT =
            createTextAttributesKey("CMAKE_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);
    public static final TextAttributesKey CONTINUATION =
            createTextAttributesKey("CMAKE_BLOCK_COMMENT", DefaultLanguageHighlighterColors.OPERATION_SIGN);


    @Override
    public @NotNull Lexer getHighlightingLexer() {
        return new CMakeLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(@NotNull IElementType tokenType) {
        if (tokenType.equals(CMakeTypes.TEXT_ELEMENT)) {
            return new TextAttributesKey[]{STRING};
        }
        if (Arrays.asList(CMakeTypes.BRACKET_CLOSE, CMakeTypes.BRACKET_OPEN).contains(tokenType)) {
            return new TextAttributesKey[]{BRACKETS};
        }
        if (Arrays.asList(CMakeTypes.PAREN_CLOSE, CMakeTypes.PAREN_OPEN).contains(tokenType)) {
            return new TextAttributesKey[]{BRACKETS};
        }
        if (tokenType.equals(CMakeTypes.COMMENT_START)) {
            return new TextAttributesKey[]{LINE_COMMENT};
        }
        if (tokenType.equals(CMakeTypes.CONTINUATION)) {
            return new TextAttributesKey[]{CONTINUATION};
        }
        if (tokenType.equals(CMakeTypes.ESCAPE_SEQUENCE)) {
            return new TextAttributesKey[]{ESCAPE};
        }
        if (tokenType.equals(CMakeTypes.QUOTE)) {
            return new TextAttributesKey[]{QUOTE_STRING};
        }
        return EMPTY_ARRAY;
    }
}
