package ros.integrate.cmake.lang;

import com.intellij.lang.Language;

public class CMakeLanguage extends Language {

    public static final Language INSTANCE = new CMakeLanguage();

    protected CMakeLanguage() {
        super("cmake");
    }
}
