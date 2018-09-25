package ros.integrate;

import com.intellij.spellchecker.BundledDictionaryProvider;

/**
 * fetches the ROS bundled dictionary into the IDE
 */
public class ROSBundledDictionaryProvider implements BundledDictionaryProvider {
    @Override
    public String[] getBundledDictionaries() {
        return new String[] {"ros.dic"};
    }
}
