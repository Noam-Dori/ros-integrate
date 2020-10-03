package ros.integrate.pkt.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.intention.UpdateStdMsgQuickFix;
import ros.integrate.pkt.psi.ROSPktElementFactory;
import ros.integrate.pkt.psi.ROSPktFieldBase;
import ros.integrate.pkt.psi.ROSPktType;
import ros.integrate.pkt.psi.ROSPktTypeBase;

import java.util.List;

/**
 * <p>Checks for use of the custom Messages types that emulate builtin types, and marks those types</p>
 * <p>for example, <code>UInt32</code> emulates <code>uint32</code> so it will be marked.</p>
 * <p>When defining custom messages, the builtin types should be used instead of the "standard types":
 *     they take up less space, and the data they hold is easier to access.
 * </p>
 * <p>this inspection offers one fix:</p>
 * <ol>
 *     <li>replace this field type with the built-in type</li>
 * </ol>
 * @author Noam Dori
 */
public class AvoidStdMsgsInspection extends ROSPktInspectionBase {
    @Override
    protected void checkField(@NotNull ROSPktFieldBase field, @NotNull InspectionManager manager, boolean isOnTheFly, @NotNull List<ProblemDescriptor> descriptors) {
        ROSPktTypeBase type = field.getTypeBase();
        if (!type.raw().getText().startsWith("std_msgs/") || type.raw().getText().equals("std_msgs/")) {
            return;
        }
        String pureMsgName = type.raw().getText().substring("std_msgs/".length()).toLowerCase();
        ROSPktType newType = ROSPktElementFactory.createType(field.getProject(),pureMsgName);
        if(newType.custom() == null) {
            String message = "Redundant use of std_msgs message type";
            ProblemDescriptor descriptor = manager.createProblemDescriptor(type.raw(), type.raw(), message,
                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly,
                    new UpdateStdMsgQuickFix());
            descriptors.add(descriptor);
        }
    }
}
