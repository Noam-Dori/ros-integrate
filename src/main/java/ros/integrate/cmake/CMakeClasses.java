package ros.integrate.cmake;

import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.lang.*;
import com.intellij.lang.folding.LanguageFolding;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import org.jetbrains.annotations.NotNull;
import ros.integrate.cmake.annotate.CMakeHomeAnnotator;
import ros.integrate.cmake.folding.CMakeFoldingBuilder;
import ros.integrate.cmake.highlight.CMakeSyntaxHighlighterFactory;
import ros.integrate.cmake.lang.CMakeCommenter;
import ros.integrate.cmake.lang.CMakeLanguage;
import ros.integrate.cmake.lang.CMakeParserDefinition;
import ros.integrate.cmake.psi.CMakeArgument;
import ros.integrate.cmake.psi.CMakeBlock;
import ros.integrate.cmake.psi.CMakeCommand;
import ros.integrate.cmake.psi.CMakeUnquotedArgument;

@SuppressWarnings("unchecked")
public interface CMakeClasses {
    boolean CLION = checkCLion();

    @NotNull
    static Class<?> getClass(String clionClassName, Class<?> internalClass) {
        try {
            return Class.forName("com.jetbrains.cmake." + clionClassName);
        } catch (ClassNotFoundException ignored) {
        }
        return internalClass;
    }

    static boolean checkCLion() {
        return PluginManagerCore.getPlugin(PluginId.getId("com.intellij.clion")) != null;
    }

    @NotNull
    static Class<? extends PsiNameIdentifierOwner> getLiteralClass() {
        return (Class<? extends PsiNameIdentifierOwner>) getClass("psi.CMakeLiteral.class", CMakeArgument.class);
    }

    static void addHomeDependencies() {
        ApplicationManager.getApplication().invokeLater(() -> {
            LanguageParserDefinitions.INSTANCE.addExplicitExtension(CMakeLanguage.INSTANCE, new CMakeParserDefinition());
            SyntaxHighlighterFactory.LANGUAGE_FACTORY.addExplicitExtension(CMakeLanguage.INSTANCE,
                    new CMakeSyntaxHighlighterFactory());
            LanguageCommenters.INSTANCE.addExplicitExtension(CMakeLanguage.INSTANCE, new CMakeCommenter());
            LanguageAnnotators.INSTANCE.addExplicitExtension(CMakeLanguage.INSTANCE, new CMakeHomeAnnotator());
            LanguageFolding.INSTANCE.addExplicitExtension(CMakeLanguage.INSTANCE, new CMakeFoldingBuilder());
            LanguageBraceMatching.INSTANCE.addExplicitExtension(CMakeLanguage.INSTANCE, new CMakeBraceMatcher());
            LanguageRefactoringSupport.INSTANCE.addExplicitExtension(CMakeLanguage.INSTANCE, new CMakeRefactoringSupportProvider());
            ElementManipulators.INSTANCE.addExplicitExtension(getUnquotedArgClass(), new CMakeUnquotedArgumentManipulator());
        });
    }

    @NotNull
    static Class<? extends PsiElement> getCommandClass() {
        return (Class<? extends PsiElement>) getClass("psi.CMakeCommand.class", CMakeCommand.class);
    }

    @NotNull
    static Class<? extends PsiElement> getUnquotedArgClass() {
        return (Class<? extends PsiElement>) getClass("psi.CMakeArgument.class", CMakeUnquotedArgument.class);
    }

    @NotNull
    static Class<? extends PsiElement> getArgClass() {
        return (Class<? extends PsiElement>) getClass("psi.CMakeArgument.class", CMakeArgument.class);
    }

    @NotNull
    static Class<? extends PsiElement> getBlockClass() {
        return (Class<? extends PsiElement>) getClass("psi.CMakeBodyBlock.class", CMakeBlock.class);
    }
}
