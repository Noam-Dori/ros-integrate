package ros.integrate.pkg.xml.intention;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.pkg.xml.DependencyType;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.VersionRange;

/**
 * an intention that adds a buildtool_depend tag to a package.xml
 * @author Noam Dori
 */
public class AddBuildtoolDependencyFix extends AddElementQuickFix {

    /**
     * construct a new intention
     * @param pkgXml the relevant package.xml file
     */
    public AddBuildtoolDependencyFix(@NotNull ROSPackageXml pkgXml) {
        super(pkgXml);
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
    public void doFix(@NotNull Editor editor) throws IncorrectOperationException {
        pkgXml.addDependency(DependencyType.BUILDTOOL, ROSPackage.ORPHAN, VersionRange.any(), null, false);
    }
}
