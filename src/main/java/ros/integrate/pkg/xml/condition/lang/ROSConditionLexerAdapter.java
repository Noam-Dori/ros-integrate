package ros.integrate.pkg.xml.condition.lang;

import com.intellij.lexer.FlexAdapter;

public class ROSConditionLexerAdapter extends FlexAdapter {
    public ROSConditionLexerAdapter() {
        super(new ROSConditionLexer(null));
    }
}
