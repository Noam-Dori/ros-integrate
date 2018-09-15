package ros.integrate.msg.psi.impl;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ros.integrate.msg.psi.ROSMsgElementFactory;
import ros.integrate.msg.psi.ROSMsgField;
import ros.integrate.msg.psi.ROSMsgLabel;

class ROSMsgLabelUtil {
    public static PsiElement set(@NotNull ROSMsgLabel label, String newName) {
        if (label.getNode() != null && !label.getText().equals(newName)) {
            ROSMsgField field = ROSMsgElementFactory.createField(label.getProject(),"dummy " + newName);
            label.replace(field.getLabel());
            return field.getLabel();
        }
        return label;
    }

    @Contract(pure = true)
    public static String getName(@NotNull ROSMsgLabel label) {
        return label.getText();
    }
}
