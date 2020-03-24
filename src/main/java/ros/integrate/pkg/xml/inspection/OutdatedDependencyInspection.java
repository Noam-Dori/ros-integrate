package ros.integrate.pkg.xml.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.xml.PackageXmlUtil;
import ros.integrate.pkg.xml.ROSPackageXml;
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
        List<ROSPackageXml.Dependency> dependencies = pkgXml.getDependencies(null);
        List<Pair<TextRange, TextRange>> depTrs = pkgXml.getDependencyTextRanges();
        List<ProblemDescriptor> ret = new ArrayList<>();
        Set<Integer> trsToAnn = new HashSet<>();
        for (int i = dependencies.size() - 1; i >= 0; i--) {
            ROSPackageXml.Dependency di = dependencies.get(i);
            String version = Optional.ofNullable(di.getPackage().getPackageXml()).map(ROSPackageXml::getVersion)
                    .orElse(null);
            if (version != null && !di.getVersionRange().contains(version)) {
                trsToAnn.add(i);
            }
        }
        trsToAnn.forEach(i -> ret.add(manager.createProblemDescriptor(file, depTrs.get(i).second, getDisplayName(),
                ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly,
                new FixDependencyQuickFix(pkgXml, i, true),
                new RemoveDependencyQuickFix(pkgXml, i))));
        return ret.toArray(ProblemDescriptor.EMPTY_ARRAY);
    }
}
