package ros.integrate.pkg.xml.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.ROSPackageXml.Version;

import java.util.Objects;

/**
 * an intention that flips the version tag value and the compatibility attribute value in that tag to ensure validity
 * @author Noam Dori
 */
public class FlipVersionCompatibilityFix extends BaseIntentionAction {
    private final ROSPackageXml pkgXml;

    /**
     * construct a new intention
     * @param pkgXml the relevant package.xml file
     */
    public FlipVersionCompatibilityFix(ROSPackageXml pkgXml) {
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
    public String getText() {
        return "Flip version value and compatibility";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return pkgXml.getVersion() != null;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        Version old = Objects.requireNonNull(pkgXml.getVersion());
        pkgXml.setVersion(new Version(old.getCompatibility(), old.getValue()));
    }
}
