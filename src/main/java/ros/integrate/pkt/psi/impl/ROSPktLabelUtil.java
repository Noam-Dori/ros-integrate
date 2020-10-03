package ros.integrate.pkt.psi.impl;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.psi.ROSPktElementFactory;
import ros.integrate.pkt.psi.ROSPktLabel;

/**
 * a utility class holding {@link ROSPktLabel} implementations
 * @author Noam Dori
 */
class ROSPktLabelUtil {
    /**
     * changes the type provided, but not the array portion
     * @param label the field label to change
     * @param newName the new name used for the field
     * @return the new (or current) psi element put in place of the provided field label.
     */
    @NotNull
    @Contract("_, _ -> param1")
    public static PsiElement set(@NotNull ROSPktLabel label, String newName) {
        if (label.getNode() != null && !label.getText().equals(newName)) {
            ROSPktLabel newLabel = ROSPktElementFactory.createLabel(label.getProject(),newName);
            label.replace(newLabel);
        }
        return label;
    }

    /**
     * Returns the name of the element.
     * @param label the field label to check
     * @return the name of the element
     * @apiNote utility function, do not use
     */
    @Contract(pure = true)
    public static String getName(@NotNull ROSPktLabel label) {
        return label.getText();
    }
}
