package ros.integrate.pkg.xml.condition.highlight;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.condition.lang.ROSConditionLexerAdapter;
import ros.integrate.pkg.xml.condition.psi.ROSConditionTypes;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class ROSConditionSyntaxHighlighter extends SyntaxHighlighterBase {
    public static final TextAttributesKey LOGIC_OPERATOR =
            createTextAttributesKey("LOGIC_OPERATOR", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey COMPARISON =
            createTextAttributesKey("COMPARISON", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey LITERAL =
            createTextAttributesKey("LITERAL", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey VARIABLE =
            createTextAttributesKey("VARIABLE", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
    public static final TextAttributesKey PARENTHESES =
            createTextAttributesKey("PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES);
    public static final TextAttributesKey IGNORED =
            createTextAttributesKey("IGNORED", CodeInsightColors.NOT_USED_ELEMENT_ATTRIBUTES);

    private static final TextAttributesKey[] LOGIC_OPERATOR_KEYS = {LOGIC_OPERATOR};
    private static final TextAttributesKey[] COMPARISON_KEYS = {COMPARISON};
    private static final TextAttributesKey[] LITERAL_KEYS = {LITERAL};
    private static final TextAttributesKey[] VARIABLE_KEYS = {VARIABLE};
    private static final TextAttributesKey[] PARENTHESES_KEYS = {PARENTHESES};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new ROSConditionLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(@NotNull IElementType tokenType) {
        if (tokenType.equals(ROSConditionTypes.LOGIC_OPERATOR)) {
            return LOGIC_OPERATOR_KEYS;
        } else if (tokenType.equals(ROSConditionTypes.COMPARISON)) {
            return COMPARISON_KEYS;
        } else if (tokenType.equals(ROSConditionTypes.LITERAL)) {
            return LITERAL_KEYS;
        } else if (tokenType.equals(ROSConditionTypes.VARIABLE)) {
            return VARIABLE_KEYS;
        } else if (tokenType.equals(ROSConditionTypes.LPARENTHESIS)) {
            return PARENTHESES_KEYS;
        } else if (tokenType.equals(ROSConditionTypes.RPARENTHESIS)) {
            return PARENTHESES_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}
