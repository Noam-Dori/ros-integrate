package ros.integrate.msg;

import com.intellij.lexer.FlexAdapter;

/**
 * a class used to enable the lexer for ROS messages.
 */
public class ROSMsgLexerAdapter extends FlexAdapter {
    public ROSMsgLexerAdapter() {
        super(new ROSMsgLexer(null));
    }
}
