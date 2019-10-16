package ros.integrate.pkt.lang;

import com.intellij.lang.*;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import ros.integrate.pkt.file.ROSPktFileType;
import ros.integrate.pkt.parser.ROSPktParser;
import ros.integrate.pkt.psi.*;
import org.jetbrains.annotations.NotNull;

/**
 * a class used to define the PSI parser for ROS messages.
 */
public class ROSPktParserDefinition implements ParserDefinition {
    private static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    private static final TokenSet COMMENTS = TokenSet.create(ROSPktTypes.COMMENT);
    private static final TokenSet STRINGS = TokenSet.create(ROSPktTypes.STRING);

    private static final IFileElementType FILE = new IFileElementType(ROSPktLanguage.INSTANCE);

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new ROSPktLexerAdapter();
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
        return STRINGS;
    }

    @NotNull
    public PsiParser createParser(final Project project) {
        return new ROSPktParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    public PsiFile createFile(FileViewProvider viewProvider) {
        if (viewProvider.getFileType() instanceof ROSPktFileType) { // should take care of all ROS pkt view providers
            return ((ROSPktFileType) viewProvider.getFileType()).newPktFile(viewProvider);
        }
        return new ROSMsgFile(viewProvider); // a default is always nice
    }

    @NotNull
    public PsiElement createElement(ASTNode node) {
        return ROSPktTypes.Factory.createElement(node);
    }
}