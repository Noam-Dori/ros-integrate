package ros.integrate;

import com.intellij.spellchecker.BundledDictionaryProvider;

public class ROSBundledDictionaryProvider implements BundledDictionaryProvider {
    @Override
    public String[] getBundledDictionaries() {
        return new String[] {"ros.dic"};
    }
}
