package ros.integrate.pkg.xml.condition.lang;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import ros.integrate.pkg.xml.condition.psi.ROSConditionTokenType;
import ros.integrate.pkg.xml.condition.psi.ROSConditionTypes;
import com.intellij.psi.TokenType;

%%

%class ROSConditionLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

CRLF=\R
WHITE_SPACE=[\ \t\f\n]
LITERAL=[^\r\n \$()][^\r\n ()]*
VARIABLE=\$[^\r\n ()]*
COMPARISON===|\!=|<=|>=|<|>
LOGIC=and|or

%states END_EXPR,START_COMPARISON,END_COMPARISON

%%

// standard condition

<START_COMPARISON> {COMPARISON}                               { yybegin(END_COMPARISON); return ROSConditionTypes.COMPARISON; }
<START_COMPARISON> {LOGIC}                                    { yybegin(END_COMPARISON); return ROSConditionTypes.LOGIC_OPERATOR; }


<END_EXPR> {LOGIC}                                            { yybegin(YYINITIAL); return ROSConditionTypes.LOGIC_OPERATOR; }
<END_EXPR> {COMPARISON}                                       { yybegin(YYINITIAL); return ROSConditionTypes.COMPARISON; }

<END_COMPARISON, END_EXPR, START_COMPARISON> {LITERAL}        { yybegin(END_EXPR); return ROSConditionTypes.LITERAL; }
<END_COMPARISON, END_EXPR, START_COMPARISON> {VARIABLE}       { yybegin(END_EXPR); return ROSConditionTypes.VARIABLE; }

<YYINITIAL> {LITERAL}                                         { yybegin(START_COMPARISON); return ROSConditionTypes.LITERAL; }
<YYINITIAL> {VARIABLE}                                        { yybegin(START_COMPARISON); return ROSConditionTypes.VARIABLE; }

\)                                                            { yybegin(END_EXPR); return ROSConditionTypes.RPARENTHESIS; }

\(                                                            { yybegin(YYINITIAL); return ROSConditionTypes.LPARENTHESIS; }

// fallback

({WHITE_SPACE}|{CRLF})+                                       { return TokenType.WHITE_SPACE; }

.                                                             { return TokenType.BAD_CHARACTER; }