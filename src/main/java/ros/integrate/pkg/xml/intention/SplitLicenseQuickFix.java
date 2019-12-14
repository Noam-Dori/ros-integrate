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

public class SplitLicenseQuickFix extends BaseIntentionAction {
    @NotNull
    private final ROSPackageXml pkgXml;
    private final int id;

    @Contract(pure = true)
    public SplitLicenseQuickFix(@NotNull ROSPackageXml pkgXml, int id) {
        this.id = id;
        this.pkgXml = pkgXml;
    }

    @NotNull
    @Override
    public String getText() {
        return "Split licenses";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS XML";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return pkgXml.getLicences().size() > id;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        String[] allLicenses = pkgXml.getLicences().get(id).split(",");
        pkgXml.removeLicense(id);
        for (String license : allLicenses) {
            pkgXml.addLicence(license);
        }
    }
}
