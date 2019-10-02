package ros.integrate.pkt.inspection;


import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.intention.UpdateKeytypeQuickFix;
import ros.integrate.pkt.psi.*;

import java.util.List;

public class DeprecatedTypeInspection extends ROSPktInspectionBase {
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull final InspectionManager manager, final boolean isOnTheFly) {
        if (!(file instanceof ROSPktFile)) return null;
        final List<ROSPktFieldBase> fields = ((ROSPktFile)file).getFields(ROSPktFieldBase.class);
        final List<ProblemDescriptor> descriptors = new SmartList<>();
        for (ROSPktFieldBase field : fields) {
            if(isSuppressedFor(field)) {continue;}
            ProgressManager.checkCanceled();
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
        return descriptors.toArray(new ProblemDescriptor[0]);
    }
}
