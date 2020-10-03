package ros.integrate.pkt.lang;

import com.intellij.lang.Language;

/**
 * the formal language definition for packet files (.msg, .srv, .action)
 * @author Noam Dori
 */
public class ROSPktLanguage extends Language {
    public static final ROSPktLanguage INSTANCE = new ROSPktLanguage();

    private ROSPktLanguage() {
        super("ROSPkt");
    }
}
