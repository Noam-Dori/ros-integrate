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

public class RemoveCompatibilityFix extends BaseIntentionAction {
    private ROSPackageXml pkgXml;

    public RemoveCompatibilityFix(ROSPackageXml pkgXml) {
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
        return "Remove version compatibility";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return pkgXml.getVersion() != null;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        pkgXml.setVersion(new Version(Objects.requireNonNull(pkgXml.getVersion()).getValue(), null));
    }
}
