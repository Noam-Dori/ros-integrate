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

import java.util.Optional;

/**
 * an intention that repairs a version tag in the package.xml (or adds it)
 * @author Noam Dori
 */
public class FixVersionQuickFix extends BaseIntentionAction {
    private final ROSPackageXml pkgXml;

    /**
     * construct a new intention
     * @param pkgXml the relevant package.xml file
     * @param prefix the intention description prefix
     */
    public FixVersionQuickFix(ROSPackageXml pkgXml, String prefix) {
        this.pkgXml = pkgXml;
        setText(prefix + " package version");
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS XML";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        Optional<Version> old = Optional.ofNullable(pkgXml.getVersion());
        pkgXml.setVersion(new Version(VersionRepairUtil.repairVersion(old.map(Version::getValue).orElse(null)),
                old.map(Version::getRawCompatibility).map(VersionRepairUtil::repairVersion).orElse(null)));
    }
}
