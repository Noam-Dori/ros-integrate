package ros.integrate.msg;

import com.intellij.lang.Language;

public class ROSMsgLanguage extends Language {
    public static final ROSMsgLanguage INSTANCE = new ROSMsgLanguage();

    private ROSMsgLanguage() {
        super("ROSMsg");
    }
}
