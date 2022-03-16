package ros.integrate.cmake.lang;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import ros.integrate.cmake.psi.CMakeTypes;
import com.intellij.psi.TokenType;

import java.util.Stack;


%%

%class CMakeLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

%{
    int bracketDepth = 0;
    // turn the state machine into a state stack
    Stack<Integer> stateStack = new Stack<>();

    void yyPush(int state) {
        stateStack.push(state);
        yybegin(state);
    }

    void yyPop() {
        stateStack.pop();
        if (stateStack.isEmpty()) {
            yybegin(YYINITIAL);
        } else {
            yybegin(stateStack.peek());
        }
    }

    String regex = "[^A-Za-z0-9]|t|r";
%}

SPACE = [ \t]
NEXTLINE = \R

%state BRACKET,PAREN,QUOTE,COMMENT

%%

<YYINITIAL, PAREN> {
    {SPACE}+                               { return TokenType.WHITE_SPACE; }
    {NEXTLINE}                             { return CMakeTypes.NEXTLINE; }
    \(                                     { yyPush(PAREN); return CMakeTypes.PAREN_OPEN;}
    #/\[=*\[                               { return CMakeTypes.COMMENT_START; }
    \[=*\[                                 { bracketDepth = yylength(); yyPush(BRACKET); return CMakeTypes.BRACKET_OPEN;}
}

<YYINITIAL> {
    #/[^\[]                                { yyPush(COMMENT); return CMakeTypes.COMMENT_START; }
    !(!([^\( \t\n#]+)|.*\[=*\[.*)          { return CMakeTypes.TEXT_ELEMENT; }
}

<PAREN> {
    \"                                     { yyPush(QUOTE); return CMakeTypes.QUOTE;}
    \)                                     { yyPop(); return CMakeTypes.PAREN_CLOSE;}
    !(!([^\) \t\n]+)|.*(\[=*\[|\").*)      { return CMakeTypes.TEXT_ELEMENT; }
}

<BRACKET> {
    ]=*]                                   { if(yylength() == bracketDepth) {
                                                 yyPop(); return CMakeTypes.BRACKET_CLOSE;
                                             } else return CMakeTypes.TEXT_ELEMENT;
                                           }
    !(!]|]=*])                             { /* ignore. Used to include single braces in the text. */ }
    [^\]]+                                 { return CMakeTypes.TEXT_ELEMENT; }
}

<QUOTE> {
    \\{NEXTLINE}                           { return CMakeTypes.CONTINUATION; }
    \\.                                    { if (String.valueOf(yycharat(1)).matches(regex)) return CMakeTypes.ESCAPE_SEQUENCE;}
    \"                                     { yyPop(); return CMakeTypes.QUOTE;}
    [^\"\\]+                               { return CMakeTypes.TEXT_ELEMENT; }
}

<COMMENT> {
    {NEXTLINE}                             { yyPop(); return CMakeTypes.NEXTLINE; }
    !(!(.+)|{NEXTLINE})                    { return CMakeTypes.TEXT_ELEMENT;}
}

.                                          { return TokenType.BAD_CHARACTER; }