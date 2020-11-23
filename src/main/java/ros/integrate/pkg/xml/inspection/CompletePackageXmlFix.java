package ros.integrate.pkg.xml.inspection;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.pkg.xml.DependencyType;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.VersionRange;
import ros.integrate.pkg.xml.intention.VersionRepairUtil;

import java.util.List;

public class CompletePackageXmlFix implements LocalQuickFix {
    private final boolean withDialog;
    @NotNull
    private final ROSPackageXml pkgXml;

    public CompletePackageXmlFix(@NotNull ROSPackageXml pkgXml, boolean withDialog) {
        this.withDialog = withDialog;
        this.pkgXml = pkgXml;
    }

    @NotNull
    @Override
    public @IntentionFamilyName String getFamilyName() {
        return "ROS XML";
    }

    @NotNull
    @Override
    public @IntentionName String getName() {
        return withDialog ? "Complete package.xml" : "Autocomplete package.xml";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        // collect relevant information
        int format = pkgXml.getFormat();
        String name = pkgXml.getPkgName(),
                description = pkgXml.getDescription();
        ROSPackageXml.Version version = pkgXml.getVersion();
        List<ROSPackageXml.Contributor> maintainers = pkgXml.getMaintainers();
        List<ROSPackageXml.License> licenses = pkgXml.getLicences();
        List<ROSPackageXml.Dependency> buildtoolDependencies = pkgXml.getDependencies(DependencyType.BUILDTOOL);

        if (format == 0) {
            pkgXml.setFormat(ROSPackageXml.getLatestFormat());
        }

        if (name == null) {
            pkgXml.setPkgName(pkgXml.getPackage().getName());
        }

        if (version == null) {
            pkgXml.setVersion(new ROSPackageXml.Version(VersionRepairUtil.repairVersion(null), null));
        }

        if (description == null) {
            pkgXml.setDescription("\nPackage description here\n");
        }

        if (maintainers.isEmpty()) {
            pkgXml.addMaintainer("user","user@todo.todo");
        }

        if (licenses.isEmpty()) {
            pkgXml.addLicence("TODO", null);
        }

        if (buildtoolDependencies.isEmpty()) {
            pkgXml.addDependency(DependencyType.BUILDTOOL, ROSPackage.ORPHAN, VersionRange.any(), null, false);
        }
    }
}
