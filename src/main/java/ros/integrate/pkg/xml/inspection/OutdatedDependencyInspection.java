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
import ros.integrate.pkg.xml.VersionRange;
import ros.integrate.pkg.xml.intention.FixDependencyQuickFix;
import ros.integrate.pkg.xml.intention.RemoveDependencyQuickFix;

import java.util.*;

public class OutdatedDependencyInspection extends LocalInspectionTool {
    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        ROSPackageXml pkgXml = PackageXmlUtil.getWrapper(file);
        if (pkgXml == null) {
            return null;
        }
        int format = pkgXml.getFormat();
        List<ROSPackageXml.Dependency> dependencies = pkgXml.getDependencies(null);
        List<TagTextRange> depTrs = pkgXml.getDependencyTextRanges();
        List<ProblemDescriptor> ret = new ArrayList<>();
        Set<Integer> trsToAnn = new HashSet<>();
        for (int i = dependencies.size() - 1; i >= 0; i--) {
            ROSPackageXml.Dependency dep = dependencies.get(i);
            if (PackageXmlUtil.conditionEvaluatesToFalse(dep, format)) {
                continue;
            }
            String depVersion = Optional.ofNullable(dep.getPackage().getPackageXml()).map(ROSPackageXml::getVersion)
                    .map(ROSPackageXml.Version::getValue).orElse(null);
            String compatibilityVersion = Optional.ofNullable(dep.getPackage().getPackageXml())
                    .map(ROSPackageXml::getVersion).map(ROSPackageXml.Version::getCompatibility).orElse(depVersion);
            VersionRange depRange = new VersionRange.Builder()
                    .min(compatibilityVersion, false).max(depVersion, false).build();
            if (depVersion != null && !depRange.isNotValid() &&
                    dep.getVersionRange().intersect(depRange) == null) {
                trsToAnn.add(i);
            }
        }
        trsToAnn.forEach(i -> ret.add(manager.createProblemDescriptor(file, depTrs.get(i).value(), getDisplayName(),
                ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly,
                new FixDependencyQuickFix(pkgXml, i, true),
                new RemoveDependencyQuickFix(pkgXml, i))));
        return ret.toArray(ProblemDescriptor.EMPTY_ARRAY);
    }
}
