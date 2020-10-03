package ros.integrate.pkt.intention;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.psi.ROSPktElementFactory;
import ros.integrate.pkt.psi.ROSPktType;

/**
 * an intention that replaces a custom field type with the built-in field type it emulates
 * @author Noam Dori
 */
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
        ROSPktType newType = ROSPktElementFactory.createType(project, pureMsgName);
        descriptor.getPsiElement().replace(newType.raw());
    }
}
