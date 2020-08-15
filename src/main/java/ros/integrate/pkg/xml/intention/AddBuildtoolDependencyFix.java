package ros.integrate.pkg.xml.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.pkg.xml.DependencyType;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.VersionRange;

public class AddBuildtoolDependencyFix extends BaseIntentionAction {
    @NotNull
    private final ROSPackageXml pkgXml;

    public AddBuildtoolDependencyFix(@NotNull ROSPackageXml pkgXml) {
        this.pkgXml = pkgXml;
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS XML";
    }

    @NotNull
    @Override
    public String getText() {
        return "Add buildtool dependency";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return pkgXml.getDependencies(DependencyType.BUILDTOOL).isEmpty();
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull Editor editor, PsiFile file) throws IncorrectOperationException {
        pkgXml.addDependency(DependencyType.BUILDTOOL, ROSPackage.ORPHAN, VersionRange.any(), null, false);
    }
}
