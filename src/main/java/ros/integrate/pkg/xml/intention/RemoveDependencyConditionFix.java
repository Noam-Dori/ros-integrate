package ros.integrate.pkg.xml.intention;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ROSPackageXml;

/**
 * an intention that removes the condition attribute from a dependency tag
 * @author Noam Dori
 */
public class RemoveDependencyConditionFix implements LocalQuickFix {
    private final int id;
    @SafeFieldForPreview
    @NotNull
    private final ROSPackageXml pkgXml;

    /**
     * construct a new intention
     * @param pkgXml the relevant package.xml file
     * @param id the index of the tag in the package.xml
     */
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
