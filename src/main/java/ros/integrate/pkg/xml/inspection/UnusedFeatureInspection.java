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
import ros.integrate.pkg.xml.TagTextRange;
import ros.integrate.pkg.xml.intention.ReformatPackageXmlFix;
import ros.integrate.pkg.xml.intention.RemoveCompatibilityFix;
import ros.integrate.pkg.xml.intention.RemoveLicenseFileFix;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UnusedFeatureInspection extends LocalInspectionTool {
    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        ROSPackageXml pkgXml = PackageXmlUtil.getWrapper(file);
        if (pkgXml == null) {
            return null;
        }
        int format = pkgXml.getFormat();
        List<ProblemDescriptor> ret = new ArrayList<>();

        if (format < 3 && Optional.ofNullable(pkgXml.getVersion()).map(ROSPackageXml.Version::getRawCompatibility)
                .isPresent()) {
            ret.add(manager.createProblemDescriptor(file, pkgXml.getVersionTextRange().attr("compatibility"),
                    "Version compatibility is only used from format 3",
                    ProblemHighlightType.LIKE_UNUSED_SYMBOL, isOnTheFly,
                    new ReformatPackageXmlFix(pkgXml, true),
                    new RemoveCompatibilityFix(pkgXml)));
        }
        List<ROSPackageXml.License> licenses = pkgXml.getLicences();
        List<TagTextRange> licenseTrs = pkgXml.getLicenceTextRanges();
        for (int i = 0; i < licenses.size(); i++) {
            ROSPackageXml.License license = licenses.get(i);
            if (format < 3 && license.getFile() != null) {
                ret.add(manager.createProblemDescriptor(file, licenseTrs.get(i).attr("file"),
                        "Version compatibility is only used from format 3",
                        ProblemHighlightType.LIKE_UNUSED_SYMBOL, isOnTheFly,
                        new ReformatPackageXmlFix(pkgXml, true),
                        new RemoveLicenseFileFix(pkgXml, i)));
            }
        }

        return ret.toArray(ProblemDescriptor.EMPTY_ARRAY);
    }
}
