package ros.integrate.pkt.intention;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.psi.ROSPktElementFactory;
import ros.integrate.pkt.psi.ROSPktType;
import ros.integrate.pkt.psi.ROSPktTypeBase;

public class UpdateKeytypeQuickFix implements LocalQuickFix {

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "Update type";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        String newName = descriptor.getPsiElement().getText().equals("char") ? "uint8" : "int8";
        ROSPktTypeBase typeToUpdate = (ROSPktTypeBase) descriptor.getPsiElement();
        ROSPktType newType = ROSPktElementFactory.createType(typeToUpdate.getProject(),newName);
        typeToUpdate.raw().replace(newType.raw());
    }
}
