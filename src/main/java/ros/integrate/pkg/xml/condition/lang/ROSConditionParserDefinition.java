package ros.integrate.pkg.xml.condition.lang;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.condition.parser.ROSConditionParser;
import ros.integrate.pkg.xml.condition.psi.ROSCondition;
import ros.integrate.pkg.xml.condition.psi.ROSConditionTypes;

/**
 * implements the details of the ROS condition language. It is not the parser, but it tells the IDE useful information
 * like what tokens are whitespaces, literals, etc. It's more like a bridge between the IDE and the parser.
 * @author Noam Dori
 */
public class ROSConditionParserDefinition implements ParserDefinition {
    public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    public static final TokenSet LITERALS = TokenSet.create(ROSConditionTypes.LITERAL);

    public static final IFileElementType FILE = new IFileElementType(ROSConditionLanguage.INSTANCE);

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new ROSConditionLexerAdapter();
    }

    @Override
    public PsiParser createParser(Project project) {
        return new ROSConditionParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return LITERALS;
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode node) {
        return ROSConditionTypes.Factory.createElement(node);
    }

    @Override
    public PsiFile createFile(FileViewProvider viewProvider) {
        return new ROSCondition(viewProvider);
    }

    @NotNull
    @Override
    public TokenSet getWhitespaceTokens() {
        return WHITE_SPACES;
    }
}
