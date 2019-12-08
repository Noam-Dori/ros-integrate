package ros.integrate.pkg.xml.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ROSPackageXml;

public class DeclareOrphanQuickFix extends BaseIntentionAction {
    private ROSPackageXml pkgXml;

    public DeclareOrphanQuickFix(ROSPackageXml pkgXml) {
        this.pkgXml = pkgXml;
    }

    @NotNull
    @Override
    public String getText() {
        return "Declare package as orphan";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS XML";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return pkgXml.getMaintainers().isEmpty();
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        pkgXml.addMaintainer("Unmaintained see http://wiki.ros.org/MaintenanceGuide#Claiming_Maintainership",
                "ros-orphaned-packages@googlegroups.com");
    }
}
