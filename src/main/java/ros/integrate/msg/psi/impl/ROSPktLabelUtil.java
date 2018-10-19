package ros.integrate.msg.psi.impl;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ros.integrate.msg.psi.ROSPktElementFactory;
import ros.integrate.msg.psi.ROSPktLabel;

/**
 * a utility class holding {@link ROSPktLabel} implementations
 */
class ROSPktLabelUtil {
    @Contract("_, _ -> param1")
    public static PsiElement set(@NotNull ROSPktLabel label, String newName) {
        if (label.getNode() != null && !label.getText().equals(newName)) {
            ROSPktLabel newLabel = ROSPktElementFactory.createLabel(label.getProject(),newName);
            label.replace(newLabel);
        }
        return label;
    }

    @Contract(pure = true)
    public static String getName(@NotNull ROSPktLabel label) {
        return label.getText();
    }
}
