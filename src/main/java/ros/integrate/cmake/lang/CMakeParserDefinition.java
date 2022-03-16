package ros.integrate.cmake.lang;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import ros.integrate.cmake.parser.CMakeParser;
import ros.integrate.cmake.psi.CMakeFile;
import ros.integrate.cmake.psi.CMakeTypes;

public class CMakeParserDefinition implements ParserDefinition {
    public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);

    public static final IFileElementType FILE = new IFileElementType(CMakeLanguage.INSTANCE);
    private static final TokenSet LITERALS = TokenSet.create(CMakeTypes.TEXT_ELEMENT);


    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new CMakeLexerAdapter();
    }

    @Override
    public PsiParser createParser(Project project) {
        return new CMakeParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    @Override
    public @NotNull TokenSet getCommentTokens() {
        return TokenSet.EMPTY;
    }

    @Override
    public @NotNull TokenSet getStringLiteralElements() {
        return LITERALS;
    }

    @Override
    public @NotNull PsiElement createElement(ASTNode node) {
        return CMakeTypes.Factory.createElement(node);
    }

    @Override
    public PsiFile createFile(FileViewProvider viewProvider) {
        return new CMakeFile(viewProvider);
    }

    @Override
    public @NotNull TokenSet getWhitespaceTokens() {
        return WHITE_SPACES;
    }
}
