package ros.integrate.pkg.xml.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.xml.PackageXmlUtil;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.intention.ReformatPackageXmlFix;

public class OutdatedFormatInspection extends LocalInspectionTool {
    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        ROSPackageXml pkgXml = PackageXmlUtil.getWrapper(file);
        if (pkgXml == null || pkgXml.getFormat() >= ROSPackageXml.getLatestFormat() ||
                pkgXml.getFormatTextRange().isEmpty()) { // this aborts the inspection since there is already an error.
            return null;
        }
        return new ProblemDescriptor[]{
                manager.createProblemDescriptor(file, pkgXml.getFormatTextRange(), getDisplayName(),
                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly,
                        new ReformatPackageXmlFix(pkgXml, true))
        };
    }
}
