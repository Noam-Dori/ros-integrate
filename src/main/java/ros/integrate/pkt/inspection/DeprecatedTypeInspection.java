package ros.integrate.pkt.inspection;


import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.intention.UpdateKeytypeQuickFix;
import ros.integrate.pkt.psi.ROSPktFieldBase;
import ros.integrate.pkt.psi.ROSPktTypeBase;

import java.util.List;

public class DeprecatedTypeInspection extends ROSPktInspectionBase {
    @Override
    protected void checkField(@NotNull ROSPktFieldBase field, @NotNull InspectionManager manager, boolean isOnTheFly, @NotNull List<ProblemDescriptor> descriptors) {
        ROSPktTypeBase type = field.getTypeBase();
        PsiElement raw = type.raw();
        if (raw.getText().equals("char") || raw.getText().equals("byte")) {
            String message = "Deprecated alias of " + (raw.getText().equals("char") ? "u" : "") + "int8";
            ProblemDescriptor descriptor = manager.createProblemDescriptor(type, type, message,
                    ProblemHighlightType.LIKE_DEPRECATED, isOnTheFly,
                    new UpdateKeytypeQuickFix());
            descriptors.add(descriptor);
        }
    }
}
