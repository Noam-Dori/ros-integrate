package ros.integrate.msg.psi.impl;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ros.integrate.msg.psi.ROSMsgElementFactory;
import ros.integrate.msg.psi.ROSMsgLabel;

/**
 * a utility class holding {@link ROSMsgLabel} implementations
 */
class ROSMsgLabelUtil {
    @Contract("_, _ -> param1")
    public static PsiElement set(@NotNull ROSMsgLabel label, String newName) {
        if (label.getNode() != null && !label.getText().equals(newName)) {
            ROSMsgLabel newLabel = ROSMsgElementFactory.createLabel(label.getProject(),newName);
            label.replace(newLabel);
        }
        return label;
    }

    @Contract(pure = true)
    public static String getName(@NotNull ROSMsgLabel label) {
        return label.getText();
    }
}
