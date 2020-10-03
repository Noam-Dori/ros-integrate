package ros.integrate.pkg.xml.condition.lang;

import com.intellij.lexer.FlexAdapter;

/**
 * an adapter for the ROS condition language lexer. Used in the complete parser and in custom syntax highlighting
 * @author Noam Dori
 */
public class ROSConditionLexerAdapter extends FlexAdapter {
    /**
     * constructs the new adapter
     */
    public ROSConditionLexerAdapter() {
        super(new ROSConditionLexer(null));
    }
}
