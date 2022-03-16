package ros.integrate.cmake.lang;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.FlexLexer;
import org.jetbrains.annotations.NotNull;

public class CMakeLexerAdapter extends FlexAdapter {
    public CMakeLexerAdapter() {
        super(new CMakeLexer(null));
    }
}
