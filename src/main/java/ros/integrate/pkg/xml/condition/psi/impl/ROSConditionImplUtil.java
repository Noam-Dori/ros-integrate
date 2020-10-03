package ros.integrate.pkg.xml.condition.psi.impl;

import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.condition.psi.ROSConditionItem;

import java.util.Optional;

/**
 * a utility class used to implement certain methods in the PSI objects within ROS conditions.
 * @author Noam Dori
 */
public class ROSConditionImplUtil {
    /**
     * checks that the verb does not contain illegal characters.
     * @param item the "verb" to check. This can either be a VARIABLE,
     *             which starts with $ and represents an environment variable,
     *             or a LITERAL, which does not start with $ and is just a plain string
     * @return true if the item does not contain any illegal characters, false otherwise.
     */
    public static boolean checkValid(@NotNull ROSConditionItem item) {
        return item.getText().matches("\\$[a-zA-Z0-9_]*|[-a-zA-Z0-9_]+");
    }

    /**
     * evaluates the actual value of the verb.
     * @param item the "verb" to check. This can either be a VARIABLE,
     *             which starts with $ and represents an environment variable,
     *             or a LITERAL, which does not start with $ and is just a plain string
     * @return for LITERAL, it just returns the text this token contains.
     *         for VARIABLE, it looks up the environment variable with the same name as the text (excluding $),
     *         and returns the result. if no such env variable exists, and empty string is returned.
     */
    @NotNull
    public static String evaluate(@NotNull ROSConditionItem item) {
        String text = item.getText();
        return text.startsWith("$") ? Optional.ofNullable(System.getenv(text.substring(1))).orElse("") : text;
    }
}
