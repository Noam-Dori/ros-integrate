package ros.integrate.pkg.xml.intention;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ROSPackageXml;

public class RemoveLicenseFileFix implements LocalQuickFix {
    private final int id;
    private final ROSPackageXml pkgXml;

    public RemoveLicenseFileFix(ROSPackageXml pkgXml, int id) {
        this.id = id;
        this.pkgXml = pkgXml;
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getName() {
        return "Remove file reference";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS XML";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        pkgXml.setLicense(id, new ROSPackageXml.License(pkgXml.getLicences().get(id).getValue(), null));
    }
}
