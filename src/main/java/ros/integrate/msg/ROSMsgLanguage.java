package ros.integrate.msg;

import com.intellij.lang.Language;

/**
 * a class used to represent the ROSMsg language
 */
public class ROSMsgLanguage extends Language {
    public static final ROSMsgLanguage INSTANCE = new ROSMsgLanguage();

    private ROSMsgLanguage() {
        super("ROSMsg");
    }
}
