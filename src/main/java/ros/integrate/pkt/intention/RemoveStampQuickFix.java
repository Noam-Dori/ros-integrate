package ros.integrate.pkt.intention;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.psi.ROSPktElementFactory;
import ros.integrate.pkt.psi.ROSPktType;

public class RemoveStampQuickFix implements LocalQuickFix {
    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "Remove stamp from type";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        PsiElement oldType = descriptor.getPsiElement();
        String newMsgName = oldType.getText().substring(0, oldType.getText().length() - "Stamped".length());
        ROSPktType newType = ROSPktElementFactory.createType(project, newMsgName);
        descriptor.getPsiElement().replace(newType.raw());
    }
}
