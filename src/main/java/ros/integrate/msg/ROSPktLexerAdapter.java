package ros.integrate.msg;

import com.intellij.lexer.FlexAdapter;

/**
 * a class used to enable the lexer for ROS messages.
 */
public class ROSPktLexerAdapter extends FlexAdapter {
    public ROSPktLexerAdapter() {
        super(new ROSPktLexer(null));
    }
}
