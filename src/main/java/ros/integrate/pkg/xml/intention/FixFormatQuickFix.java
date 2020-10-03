package ros.integrate.pkg.xml.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ROSPackageXml;

/**
 * an intention that repairs the format attribute of the file by just setting it to the latest version
 * @author Noam Dori
 */
public class FixFormatQuickFix extends BaseIntentionAction implements LocalQuickFix {
    private final ROSPackageXml pkgXml;

    /**
     * construct a new intention
     * @param pkgXml the relevant package.xml file
     */
    public FixFormatQuickFix(ROSPackageXml pkgXml) {
        this.pkgXml = pkgXml;
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS XML";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getName() {
        return "Fix format";
    }

    @NotNull
    @Override
    public String getText() {
        return getName();
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        pkgXml.setFormat(ROSPackageXml.getLatestFormat());
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return pkgXml.getFormat() != ROSPackageXml.getLatestFormat();
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        pkgXml.setFormat(ROSPackageXml.getLatestFormat());
    }
}
