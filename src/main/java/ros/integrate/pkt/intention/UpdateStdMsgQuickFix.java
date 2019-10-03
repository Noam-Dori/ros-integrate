package ros.integrate.pkt.intention;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.psi.ROSPktElementFactory;
import ros.integrate.pkt.psi.ROSPktType;

public class UpdateStdMsgQuickFix implements LocalQuickFix {

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "Use builtin type";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        String pureMsgName = descriptor.getPsiElement().getText().substring("std_msgs/".length()).toLowerCase();
        ROSPktType newType = ROSPktElementFactory.createType(descriptor.getPsiElement().getProject(), pureMsgName);
        descriptor.getPsiElement().replace(newType.raw());
    }
}
