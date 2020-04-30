package ros.integrate.pkg.xml.condition.psi.impl;

import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.condition.psi.ROSConditionItem;

import java.util.Optional;

public class ROSConditionImplUtil {
    public static boolean checkValid(@NotNull ROSConditionItem item) {
        return item.getText().matches("\\$[a-zA-Z0-9_]*|[-a-zA-Z0-9_]+");
    }

    @NotNull
    public static String evaluate(@NotNull ROSConditionItem item) {
        String text = item.getText();
        return text.startsWith("$") ? Optional.ofNullable(System.getenv(text.substring(1))).orElse("") : text;
    }
}
