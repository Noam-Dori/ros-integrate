package ros.integrate.pkg.xml.condition.lang;

import com.intellij.lang.Language;

/**
 * the formal language definition for ROS conditions.
 * @author Noam Dori
 */
public class ROSConditionLanguage extends Language {
    public static final ROSConditionLanguage INSTANCE = new ROSConditionLanguage();

    /**
     * constructs the ROS condition instance
     */
    protected ROSConditionLanguage() {
        super("ROSCondition");
    }
}
