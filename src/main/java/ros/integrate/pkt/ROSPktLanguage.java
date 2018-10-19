package ros.integrate.pkt;

import com.intellij.lang.Language;

/**
 * a class used to represent the ROSMsg language
 */
public class ROSPktLanguage extends Language {
    public static final ROSPktLanguage INSTANCE = new ROSPktLanguage();

    private ROSPktLanguage() {
        super("ROSPkt");
    }
}
