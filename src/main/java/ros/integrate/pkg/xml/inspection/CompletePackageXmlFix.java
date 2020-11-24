package ros.integrate.pkg.xml.inspection;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.pkg.xml.DependencyType;
import ros.integrate.pkg.xml.PackageXmlUtil;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.VersionRange;
import ros.integrate.pkg.xml.ui.PackageXmlDialog;
import ros.integrate.pkg.xml.intention.VersionRepairUtil;
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
        if (withDialog) {
            doDialogFix(project);
        } else {
            doAutoFix();
        }
    }

    private void doDialogFix(Project project) {
        PackageXmlDialog dialog = new PackageXmlDialog(project, pkgXml);
        ApplicationManager.getApplication().invokeLater(() -> {
            dialog.show();
            WriteCommandAction.runWriteCommandAction(project, () -> {
                if (dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
                    PackageXmlUtil.overwrite(dialog, pkgXml);
                }
            });
        });

    }

    private void doAutoFix() {

        if (pkgXml.getFormat() == 0) {
            pkgXml.setFormat(ROSPackageXml.getLatestFormat());
        }

        if (pkgXml.getPkgName() == null) {
            pkgXml.setPkgName(pkgXml.getPackage().getName());
        }

        if (pkgXml.getVersion() == null) {
            pkgXml.setVersion(new ROSPackageXml.Version(VersionRepairUtil.repairVersion(null), null));
        }

        if (pkgXml.getDescription() == null) {
            pkgXml.setDescription("\nPackage description here\n");
        }

        if (pkgXml.getMaintainers().isEmpty()) {
            pkgXml.addMaintainer("user","user@todo.todo");
        }

        if (pkgXml.getLicences().isEmpty()) {
            pkgXml.addLicense("TODO", null);
        }

        if (pkgXml.getDependencies(DependencyType.BUILDTOOL).isEmpty()) {
            pkgXml.addDependency(DependencyType.BUILDTOOL, ROSPackage.ORPHAN, VersionRange.any(), null, false);
        }
    }
}
