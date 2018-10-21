package ros.integrate.pkt.lang;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import ros.integrate.pkt.psi.ROSPktTypes;
import com.intellij.psi.TokenType;

%%

%class ROSPktLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

CRLF=\R
WHITE_SPACE=[\ \t\f]
NAME_CHARACTER=[^\r\n =]
TYPE_CHARACTER=[^\r\n \[]
LINE_COMMENT=("#")[^\r\n]*
CONST_ASSIGNER="="
NUMBER=[0-9]
ARRAY_LEAD="["
ARRAY_END="]"
NON_NUMERICAL=[^\n\ 0-9\.-]
NON_NUMERICAL_4FRAG=[^\n\ 0-9\.=-]
CONST_STRING=[^\n]
FIRST_STRING=[^\n\ ]
FIRST_STRING_4FRAG=[^\n\ =\#]
END_STR_SEQ={CONST_STRING}*{FIRST_STRING}
START_STR_SEQ={FIRST_STRING}{CONST_STRING}*
START_STR_SEQ_4FRAG={FIRST_STRING_4FRAG}{CONST_STRING}*
KEYTYPE_INT=u?int(8|16|32|64)
KEYTYPE_FLOAT=float(32|64)
KEYTYPE_TIME=(time)|(duration)
KEYTYPE_STRING=string
KEYTYPE_BOOL=bool
KEYTYPE_NUM={KEYTYPE_BOOL}|{KEYTYPE_INT}|{KEYTYPE_FLOAT}
KEYTYPE_OTHER={KEYTYPE_STRING}|{KEYTYPE_TIME}

MULTI_PERIOD_STR={START_STR_SEQ}?\.{CONST_STRING}*\.{END_STR_SEQ}?
MULTI_PERIOD_STR_4FRAG={START_STR_SEQ_4FRAG}?\.{CONST_STRING}*\.{END_STR_SEQ}?

NON_NUMERICAL_STR={START_STR_SEQ}?{NON_NUMERICAL}{END_STR_SEQ}?|{START_STR_SEQ}\ {END_STR_SEQ}
NON_NUMERICAL_STR_4FRAG={START_STR_SEQ_4FRAG}?{NON_NUMERICAL_4FRAG}{END_STR_SEQ}?|{START_STR_SEQ_4FRAG}\ {END_STR_SEQ}

BAD_NEG_STR={START_STR_SEQ}-{END_STR_SEQ}?
BAD_NEG_STR_4FRAG={START_STR_SEQ_4FRAG}-{END_STR_SEQ}?

STR_CONST={BAD_NEG_STR}|{MULTI_PERIOD_STR}|{NON_NUMERICAL_STR}
STR_CONST_4FRAG={BAD_NEG_STR_4FRAG}|{MULTI_PERIOD_STR_4FRAG}|{NON_NUMERICAL_STR_4FRAG}

FLOAT={NUMBER}+(\.)?{NUMBER}*|{NUMBER}*(\.)?{NUMBER}+

%states END_TYPE, IN_ARRAY, END_ARRAY, START_NAME, END_NAME, START_CONST, END_LINE
%states END_INT_TYPE, IN_INT_ARRAY, END_INT_ARRAY, START_INT_NAME, END_INT_NAME, START_INT_CONST, NEG_NUM
%states START_CONST_FRAG, START_INT_CONST_FRAG

%%

// standard message

<YYINITIAL> ---                                             { yybegin(END_LINE); return ROSPktTypes.SERVICE_SEPARATOR; }

<YYINITIAL,END_NAME,END_INT_NAME,START_CONST_FRAG,START_INT_CONST_FRAG> {LINE_COMMENT}
                                                            { yybegin(YYINITIAL); return ROSPktTypes.LINE_COMMENT; }

<YYINITIAL> {KEYTYPE_NUM}                                   { yybegin(END_INT_TYPE); return ROSPktTypes.KEYTYPE; }
<YYINITIAL> {KEYTYPE_OTHER}                                 { yybegin(END_TYPE); return ROSPktTypes.KEYTYPE; }
<YYINITIAL> {TYPE_CHARACTER}+                               { yybegin(END_TYPE); return ROSPktTypes.CUSTOM_TYPE; }

<END_TYPE> {ARRAY_LEAD}                                     { yybegin(IN_ARRAY); return ROSPktTypes.LBRACKET; }
<END_INT_TYPE> {ARRAY_LEAD}                                 { yybegin(IN_INT_ARRAY); return ROSPktTypes.LBRACKET; }

<IN_ARRAY> {NUMBER}+                                        { yybegin(IN_ARRAY); return ROSPktTypes.NUMBER; }
<IN_INT_ARRAY> {NUMBER}+                                    { yybegin(IN_INT_ARRAY); return ROSPktTypes.NUMBER; }

<IN_ARRAY> {ARRAY_END}                                      { yybegin(END_ARRAY); return ROSPktTypes.RBRACKET; }
<IN_INT_ARRAY> {ARRAY_END}                                  { yybegin(END_INT_ARRAY); return ROSPktTypes.RBRACKET; }

<END_ARRAY,END_TYPE> {WHITE_SPACE}+                         { yybegin(START_NAME); return TokenType.WHITE_SPACE; }
<END_INT_ARRAY,END_INT_TYPE> {WHITE_SPACE}+                 { yybegin(START_INT_NAME); return TokenType.WHITE_SPACE; }

<START_NAME> {NAME_CHARACTER}+                              { yybegin(END_NAME); return ROSPktTypes.NAME; }
<START_INT_NAME> {NAME_CHARACTER}+                          { yybegin(END_INT_NAME); return ROSPktTypes.NAME; }

<END_NAME> {WHITE_SPACE}+                                   { yybegin(START_CONST_FRAG); return TokenType.WHITE_SPACE; }
<END_INT_NAME> {WHITE_SPACE}+                               { yybegin(START_INT_CONST_FRAG); return TokenType.WHITE_SPACE; }

<END_NAME,START_CONST_FRAG> {CONST_ASSIGNER}                { yybegin(START_CONST); return ROSPktTypes.CONST_ASSIGNER; }
<END_INT_NAME,START_INT_CONST_FRAG> {CONST_ASSIGNER}        { yybegin(START_INT_CONST); return ROSPktTypes.CONST_ASSIGNER; }

<START_CONST> {WHITE_SPACE}+                                { yybegin(START_CONST); return TokenType.WHITE_SPACE; }
<START_INT_CONST> {WHITE_SPACE}+                            { yybegin(START_INT_CONST); return TokenType.WHITE_SPACE; }

<START_INT_CONST,START_INT_CONST_FRAG> -                    { yybegin(NEG_NUM); return ROSPktTypes.NEG_OPERATOR; }
<NEG_NUM,START_INT_CONST,START_INT_CONST_FRAG> {FLOAT}      { yybegin(END_LINE); return ROSPktTypes.NUMBER;}

<START_INT_CONST> {STR_CONST}                               { yybegin(END_LINE); return ROSPktTypes.STRING;}

<START_CONST> {FIRST_STRING}{END_STR_SEQ}?                  { yybegin(END_LINE); return ROSPktTypes.STRING;}

<YYINITIAL> {WHITE_SPACE}+                                  { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

// fragmentation

<IN_ARRAY> {WHITE_SPACE}+                                   { yybegin(START_NAME); return TokenType.WHITE_SPACE;}
<IN_INT_ARRAY> {WHITE_SPACE}+                               { yybegin(START_INT_NAME); return TokenType.WHITE_SPACE;}

<START_CONST_FRAG> {WHITE_SPACE}+                           { yybegin(START_CONST_FRAG); return TokenType.WHITE_SPACE; }
<START_INT_CONST_FRAG> {WHITE_SPACE}+                       { yybegin(START_INT_CONST_FRAG); return TokenType.WHITE_SPACE; }

<START_INT_CONST_FRAG> {STR_CONST_4FRAG}                    { yybegin(END_LINE); return ROSPktTypes.STRING;}
<START_CONST_FRAG> {FIRST_STRING_4FRAG}{END_STR_SEQ}?       { yybegin(END_LINE); return ROSPktTypes.STRING;}

// terminator

{WHITE_SPACE}*{CRLF}                                        { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

// fallback

{WHITE_SPACE}+                                              { return TokenType.WHITE_SPACE; }

.                                                           { return TokenType.BAD_CHARACTER; }
