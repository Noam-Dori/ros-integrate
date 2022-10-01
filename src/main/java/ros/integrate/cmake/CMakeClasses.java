package ros.integrate.cmake;

import com.intellij.lang.LanguageAnnotators;
import com.intellij.lang.LanguageCommenters;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.folding.LanguageFolding;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.psi.PsiNameIdentifierOwner;
import org.jetbrains.annotations.Nullable;
import ros.integrate.cmake.annotate.CMakeHomeAnnotator;
import ros.integrate.cmake.folding.CMakeFoldingBuilder;
import ros.integrate.cmake.highlight.CMakeSyntaxHighlighterFactory;
import ros.integrate.cmake.lang.CMakeCommenter;
import ros.integrate.cmake.lang.CMakeLanguage;
import ros.integrate.cmake.lang.CMakeParserDefinition;

@SuppressWarnings("unchecked")
public interface CMakeClasses {
    boolean CLION = checkCLion();

    @Nullable
    static Class<?> getClass(String clionClassName, String pluginClassName) {
        try {
            return Class.forName("com.jetbrains.cmake." + clionClassName);
        } catch (ClassNotFoundException ignored) {}
        try {
            return Class.forName("com.cmakeplugin." + pluginClassName);
        } catch (ClassNotFoundException ignored) {}
        return null;
    }

    static boolean checkCLion() {
        try {
            Class.forName("com.jetbrains.cmake.CMakeLanguage");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Nullable
    static Class<? extends PsiNameIdentifierOwner> getCMakeArgClass() {
        return (Class<? extends PsiNameIdentifierOwner>)
                getClass("psi.CMakeLiteral.class",
                        "psi.CMakeUnquotedArgumentMaybeVariableContainer");
    }

    static void addHomeDependencies() {
        LanguageParserDefinitions.INSTANCE.addExplicitExtension(CMakeLanguage.INSTANCE, new CMakeParserDefinition());
        SyntaxHighlighterFactory.LANGUAGE_FACTORY.addExplicitExtension(CMakeLanguage.INSTANCE,
                new CMakeSyntaxHighlighterFactory());
        LanguageCommenters.INSTANCE.addExplicitExtension(CMakeLanguage.INSTANCE, new CMakeCommenter());
        LanguageAnnotators.INSTANCE.addExplicitExtension(CMakeLanguage.INSTANCE, new CMakeHomeAnnotator());
        LanguageFolding.INSTANCE.addExplicitExtension(CMakeLanguage.INSTANCE, new CMakeFoldingBuilder());
    }
}
