package ros.integrate.pkg.xml.condition.lang;

import com.intellij.lang.Language;

public class ROSConditionLanguage extends Language {
    public static final ROSConditionLanguage INSTANCE = new ROSConditionLanguage();

    protected ROSConditionLanguage() {
        super("ROSCondition");
    }
}
