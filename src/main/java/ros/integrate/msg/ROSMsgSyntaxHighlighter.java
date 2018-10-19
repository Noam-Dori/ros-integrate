package ros.integrate.msg;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.ui.JBColor;
import ros.integrate.msg.psi.ROSPktTypes;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

/**
 * a highlighter for ROS messages and services.
 */
public class ROSMsgSyntaxHighlighter extends SyntaxHighlighterBase {
    private static final TextAttributesKey ASSIGNER =
            createTextAttributesKey("ROSMSG_CONST_ASSIGNER", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    private static final TextAttributesKey NEG_NUMBER =
            createTextAttributesKey("ROSMSG_CONST_ASSIGNER", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    private static final TextAttributesKey BRACKET =
            createTextAttributesKey("ROSMSG_BRACKET", DefaultLanguageHighlighterColors.BRACKETS);
    static final TextAttributesKey NUMBER =
            createTextAttributesKey("ROSMSG_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    static final TextAttributesKey STRING =
            createTextAttributesKey("ROSMSG_STRING", DefaultLanguageHighlighterColors.STRING);
    static final TextAttributesKey TYPE =
            createTextAttributesKey("ROSMSG_TYPE", DefaultLanguageHighlighterColors.CLASS_NAME);
    static final TextAttributesKey KEYTYPE =
            createTextAttributesKey("ROSMSG_KEYTYPE", DefaultLanguageHighlighterColors.KEYWORD);
    static final TextAttributesKey NAME =
            createTextAttributesKey("ROSMSG_NAME", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
    static final TextAttributesKey COMMENT =
            createTextAttributesKey("ROSMSG_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    private static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("ROSMSG_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);
    private static final JBColor SEPARATOR = new JBColor(new Color(0x006666),new Color(0x0F9795));
    @SuppressWarnings("deprecation") // I getValue it, but there is no default for separators...
    static final TextAttributesKey SERVICE =
            createTextAttributesKey("ROSMSG_SERVICE_SEPARATOR",
                    new TextAttributes(SEPARATOR, null, null, EffectType.STRIKEOUT, Font.BOLD));

    private static final TextAttributesKey[] SERVICE_KEYS = new TextAttributesKey[]{SERVICE};
    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] ASSIGNER_KEYS = new TextAttributesKey[]{ASSIGNER};
    private static final TextAttributesKey[] NEG_NUM_KEYS = new TextAttributesKey[]{NEG_NUMBER};
    private static final TextAttributesKey[] TYPE_KEYS = new TextAttributesKey[]{TYPE};
    private static final TextAttributesKey[] KEYTYPE_KEYS = new TextAttributesKey[]{KEYTYPE};
    private static final TextAttributesKey[] NAME_KEYS = new TextAttributesKey[]{NAME};
    private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{NUMBER};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
    private static final TextAttributesKey[] BRACKET_KEYS = new TextAttributesKey[]{BRACKET};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new ROSPktLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(ROSPktTypes.LINE_COMMENT)) {
            return COMMENT_KEYS;
        } else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else if (tokenType.equals(ROSPktTypes.LBRACKET)) {
            return BRACKET_KEYS;
        } else if (tokenType.equals(ROSPktTypes.RBRACKET)) {
            return BRACKET_KEYS;
        } else if (tokenType.equals(ROSPktTypes.STRING)) {
            return STRING_KEYS;
        } else if (tokenType.equals(ROSPktTypes.NUMBER)) {
            return NUMBER_KEYS;
        } else if (tokenType.equals(ROSPktTypes.NAME)) {
            return NAME_KEYS;
        } else if (tokenType.equals(ROSPktTypes.KEYTYPE)) {
            return KEYTYPE_KEYS;
        } else if (tokenType.equals(ROSPktTypes.CUSTOM_TYPE)) {
            return TYPE_KEYS;
        } else if (tokenType.equals(ROSPktTypes.CONST_ASSIGNER)) {
            return ASSIGNER_KEYS;
        } else if (tokenType.equals(ROSPktTypes.NEG_OPERATOR)) {
            return NEG_NUM_KEYS;
        } else if (tokenType.equals(ROSPktTypes.SERVICE_SEPARATOR)) {
            return SERVICE_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}
