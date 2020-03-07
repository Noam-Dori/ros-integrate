package ros.integrate.pkg.xml.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.ROSPackageManager;
import ros.integrate.pkg.xml.PackageXmlUtil;

public class ExcludePackageXml extends BaseIntentionAction implements LocalQuickFix {
    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS XML";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        project.getComponent(ROSPackageManager.class).excludePkgXml((XmlFile) descriptor.getPsiElement()
                .getContainingFile());
    }

    @NotNull
    @Override
    public String getText() {
        return "Exclude file from ROS";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return file instanceof XmlFile && PackageXmlUtil.getWrapper(file) != null;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        project.getComponent(ROSPackageManager.class).excludePkgXml((XmlFile) file);
    }
}
