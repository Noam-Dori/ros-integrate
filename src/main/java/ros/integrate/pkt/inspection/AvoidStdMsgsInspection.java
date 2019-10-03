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

public class AvoidStdMsgsInspection extends ROSPktInspectionBase {
    @Override
    protected void checkField(@NotNull ROSPktFieldBase field, @NotNull InspectionManager manager, boolean isOnTheFly, @NotNull List<ProblemDescriptor> descriptors) {
        ROSPktTypeBase type = field.getTypeBase();
        if (!type.raw().getText().startsWith("std_msgs/")) {
            return;
        }
        String pureMsgName = type.raw().getText().substring("std_msgs/".length()).toLowerCase();
        if(pureMsgName.isEmpty()) {
            return;
        }
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
