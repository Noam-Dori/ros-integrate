package ros.integrate.pkt.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.Consumer;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.intention.RemoveStampQuickFix;
import ros.integrate.pkt.psi.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Checks if there is an available variant of the type provided that does not have a stamp.
 * Note: this makes the assumption that message {@code [NAME]Stamped} is the stamped alternative of {@code [NAME]}
 */
public class RedundantStampingInspection extends ROSPktInspectionBase {
    @SuppressWarnings("WeakerAccess")
    public boolean CHECK_ARRAY = false;
    @SuppressWarnings("WeakerAccess")
    public boolean CHECK_VECTOR = false;
    @SuppressWarnings("WeakerAccess")
    public boolean CHECK_OBJECT = true;

    @Override
    protected void checkField(@NotNull ROSPktFieldBase field, @NotNull InspectionManager manager, boolean isOnTheFly, @NotNull List<ProblemDescriptor> descriptors) {
        ROSPktTypeBase type = field.getTypeBase();
        String oldName = type.raw().getText();
        if (!oldName.endsWith("Stamped") || oldName.equals("Stamped")) {
            return;
        }

        ROSPktType resolvableNewType = ROSPktElementFactory.createType(field.getProject(),
                getResolvableName(oldName, field));
        if (resolvableNewType.getReference().resolve() != null && mayRaiseProblem(type.size())) {
            String message = "Potentially redundant use of header-stamped message";
            ProblemDescriptor descriptor = manager.createProblemDescriptor(type.raw(), type.raw(), message,
                    ProblemHighlightType.WEAK_WARNING, isOnTheFly,
                    new RemoveStampQuickFix());
            descriptors.add(descriptor);
        }
    }

    @Contract(pure = true)
    private boolean mayRaiseProblem(int size) {
        switch (size) {
            case -1:
                return CHECK_OBJECT;
            case 0:
                return CHECK_VECTOR;
            default:
                return CHECK_ARRAY;
        }
    }

    @NotNull
    private String getResolvableName(@NotNull String oldName, ROSPktFieldBase field) {
        return (oldName.contains("/") ? "" : field.getContainingFile().getPackage().getName() + "/") +
                oldName.substring(0, oldName.length() - "Stamped".length());
    }

    @NotNull
    @Override
    public JComponent createOptionsPanel() {
        JPanel unalignedPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent("Inspect objects",
                        bindCheckBox(CHECK_OBJECT, checkBox -> CHECK_OBJECT = checkBox.isSelected()))
                .addLabeledComponent("Inspect arrays",
                        bindCheckBox(CHECK_ARRAY, checkBox -> CHECK_ARRAY = checkBox.isSelected()))
                .addLabeledComponent("Inspect lists",
                        bindCheckBox(CHECK_VECTOR, checkBox -> CHECK_VECTOR = checkBox.isSelected()))
                .getPanel();
        JPanel ret = new JPanel(new BorderLayout());
        ret.add(unalignedPanel, BorderLayout.NORTH);
        return ret;
    }

    @NotNull
    private JBCheckBox bindCheckBox(boolean loadValue, Consumer<JBCheckBox> onChange) {
        JBCheckBox ret = new JBCheckBox(null, loadValue);
        ret.addChangeListener(event -> onChange.consume(ret));
        return ret;
    }
}
