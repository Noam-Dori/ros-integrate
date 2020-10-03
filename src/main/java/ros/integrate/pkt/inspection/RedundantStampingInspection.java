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
 * <p>Checks for use of stamped types within a message definition
 *     (for example, PointStamped is a stamped variant of Point) and if a non-stamped alternative exists.</p>
 * <p>Using stamped types is generally discouraged,
 *     so a <code>std_msgs/Header</code> is placed instead at the top level of the message</p>
 * <p>However, there are many exceptions to this rule, like nav_msgs/Path which may need to track an object relative to time.
 * </p>
 * <p>This inspection has many options that may be toggled in the inspection profile.
 * </p>
 * <p>sometimes there are exceptions
 *     (like <code>nav_msgs/Path</code> containing an array of <code>std_msgs/PointStamped</code>)
 *     so you can configure what field types will be scanned:</p>
 * <ul>
 *     <li>
 *         Inspect objects: will raise errors for stamped types without an array descriptor.<br/>
 *         Example: <code>geometry_msgs/PoseStamped</code>
 *     </li>
 *     <li>
 *         Inspect arrays: will raise errors for stamped types that are finite arrays<br/>
 *         Example: <code>geometry_msgs/PoseStamped[27]</code>
 *     </li>
 *     <li>
 *         Inspect lists: will raise errors for stamped types that are dynamic arrays (vector/list)<br/>
 *         Example: <code>geometry_msgs/PoseStamped[]</code>)
 *     </li>
 * </ul>
 * <p>by default, the only types checked are object types.</p>
 * <p>this inspection offers one fix:</p>
 * <ol>
 *     <li>convert the stamped type to the corresponding non-stamped type (for example, PointStamped -> Point)</li>
 * </ol>
 * @author Noam Dori
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
