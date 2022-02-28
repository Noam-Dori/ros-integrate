package ros.integrate.cmake;

import com.intellij.psi.PsiNameIdentifierOwner;
import org.jetbrains.annotations.Nullable;

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
}
