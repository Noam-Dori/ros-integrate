package ros.integrate.pkg.xml.intention;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ROSPackageXml;

public class RemoveDependencyConditionFix implements LocalQuickFix {
    private final int id;
    @NotNull
    private final ROSPackageXml pkgXml;

    public RemoveDependencyConditionFix(@NotNull ROSPackageXml pkgXml, int id) {
        this.id = id;
        this.pkgXml = pkgXml;
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getName() {
        return "Remove condition";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS XML";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        ROSPackageXml.Dependency old = pkgXml.getDependencies(null).get(id);
        pkgXml.setDependency(id, new ROSPackageXml.Dependency(old.getType(), old.getPackage(), old.getVersionRange(),
                null));
    }
}
