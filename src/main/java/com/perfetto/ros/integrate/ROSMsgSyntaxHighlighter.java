package com.perfetto.ros.integrate;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.ui.JBColor;
import com.perfetto.ros.integrate.psi.ROSMsgTypes;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class ROSMsgSyntaxHighlighter extends SyntaxHighlighterBase {
    public static final TextAttributesKey ASSIGNER =
            createTextAttributesKey("ROSMSG_CONST_ASSIGNER", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey NEG_NUMBER =
            createTextAttributesKey("ROSMSG_CONST_ASSIGNER", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey BRACKET =
            createTextAttributesKey("ROSMSG_BRACKET", DefaultLanguageHighlighterColors.BRACKETS);
    public static final TextAttributesKey NUMBER =
            createTextAttributesKey("ROSMSG_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey STRING =
            createTextAttributesKey("ROSMSG_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey TYPE =
            createTextAttributesKey("ROSMSG_TYPE", DefaultLanguageHighlighterColors.CLASS_NAME);
    public static final TextAttributesKey KEYTYPE =
            createTextAttributesKey("ROSMSG_KEYTYPE", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey NAME =
            createTextAttributesKey("ROSMSG_NAME", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
    public static final TextAttributesKey COMMENT =
            createTextAttributesKey("ROSMSG_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("ROSMSG_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);
    public static final JBColor SEPARATOR = new JBColor(new Color(0x006666),new Color(0x0F9795));
    @SuppressWarnings("deprecation") // I get it, but there is no default for separators...
    public static final TextAttributesKey SERVICE =
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
        return new ROSMsgLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(ROSMsgTypes.COMMENT)) {
            return COMMENT_KEYS;
        } else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else if (tokenType.equals(ROSMsgTypes.LBRACKET)) {
            return BRACKET_KEYS;
        } else if (tokenType.equals(ROSMsgTypes.RBRACKET)) {
            return BRACKET_KEYS;
        } else if (tokenType.equals(ROSMsgTypes.STRING)) {
            return STRING_KEYS;
        } else if (tokenType.equals(ROSMsgTypes.NUMBER)) {
            return NUMBER_KEYS;
        } else if (tokenType.equals(ROSMsgTypes.NAME)) {
            return NAME_KEYS;
        } else if (tokenType.equals(ROSMsgTypes.KEYTYPE)) {
            return KEYTYPE_KEYS;
        } else if (tokenType.equals(ROSMsgTypes.TYPE)) {
            return TYPE_KEYS;
        } else if (tokenType.equals(ROSMsgTypes.CONST_ASSIGNER)) {
            return ASSIGNER_KEYS;
        } else if (tokenType.equals(ROSMsgTypes.NEG_OPERATOR)) {
            return NEG_NUM_KEYS;
        } else if (tokenType.equals(ROSMsgTypes.SERVICE_SEPERATOR)) {
            return SERVICE_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}
