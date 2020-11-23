package ros.integrate.pkg.xml.inspection;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ROSPackageXml;

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

    }
}
