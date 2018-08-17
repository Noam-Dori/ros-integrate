package ros.integrate.msg;

import com.intellij.lexer.FlexAdapter;

public class ROSMsgLexerAdapter extends FlexAdapter {
    public ROSMsgLexerAdapter() {
        super(new ROSMsgLexer(null));
    }
}
