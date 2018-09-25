package ros.integrate.msg;

import com.intellij.lang.*;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import ros.integrate.msg.parser.ROSMsgParser;
import ros.integrate.msg.psi.*;
import org.jetbrains.annotations.NotNull;

/**
 * a class used to define the PSI parser for ROS messages.
 */
public class ROSMsgParserDefinition implements ParserDefinition {
    public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    public static final TokenSet COMMENTS = TokenSet.create(ROSMsgTypes.COMMENT);

    public static final IFileElementType FILE = new IFileElementType(ROSMsgLanguage.INSTANCE);

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new ROSMsgLexerAdapter();
    }

    @NotNull
    public TokenSet getWhitespaceTokens() {
        return WHITE_SPACES;
    }

    @NotNull
    public TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @NotNull
    public TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @NotNull
    public PsiParser createParser(final Project project) {
        return new ROSMsgParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    public PsiFile createFile(FileViewProvider viewProvider) {
        return new ROSMsgFile(viewProvider);
    }

    @NotNull
    public PsiElement createElement(ASTNode node) {
        return ROSMsgTypes.Factory.createElement(node);
    }
}