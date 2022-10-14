package ros.integrate.cmake.lang;

import com.intellij.lang.Language;

/**
 * Defines the CMake language for non CLion-IDEs
 * @author Noam Dori
 */
public class CMakeLanguage extends Language {

    public static final Language INSTANCE = new CMakeLanguage();

    protected CMakeLanguage() {
        super("CMake");
    }
}
