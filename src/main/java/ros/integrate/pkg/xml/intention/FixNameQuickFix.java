package ros.integrate.pkg.xml.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ROSPackageXml;

public class FixNameQuickFix extends BaseIntentionAction {
    private ROSPackageXml pkgXml;

    @Contract(pure = true)
    public FixNameQuickFix(ROSPackageXml pkgXml, String prefix) {
        this.pkgXml = pkgXml;
        setText(prefix + " package name");
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS XML";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return !pkgXml.getPackage().getName().equals(pkgXml.getPkgName());
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        pkgXml.setPkgName(pkgXml.getPackage().getName());
    }
}
