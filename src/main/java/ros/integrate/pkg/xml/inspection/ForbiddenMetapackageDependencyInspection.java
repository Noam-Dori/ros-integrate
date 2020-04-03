package ros.integrate.pkg.xml.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.xml.DependencyType;
import ros.integrate.pkg.xml.PackageXmlUtil;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.intention.RemoveDependencyQuickFix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ForbiddenMetapackageDependencyInspection extends LocalInspectionTool {
    private static final List<DependencyType> ALLOWED_DEP_TYPES =
            Arrays.asList(DependencyType.EXEC, DependencyType.BUILDTOOL);

    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        ROSPackageXml pkgXml = PackageXmlUtil.getWrapper(file);
        if (pkgXml == null ||
                Optional.ofNullable(pkgXml.getExport()).map(export -> !export.isMetapackage()).orElse(true)) {
            return null;
        }
        List<ROSPackageXml.Dependency> dependencies = pkgXml.getDependencies(null);
        List<Pair<TextRange, TextRange>> depTrs = pkgXml.getDependencyTextRanges();
        List<ProblemDescriptor> ret = new ArrayList<>();
        Integer firstBuildtoolDependency = null;
        boolean buildtoolWarningRaised = false;
        for (int i = dependencies.size() - 1; i >= 0; i--) {
            DependencyType targetType = dependencies.get(i).getType();
            List<DependencyType> coveredTypes = Arrays.asList(targetType.getCoveredDependencies());
            if (!ALLOWED_DEP_TYPES.containsAll(coveredTypes)) {
                raiseWarning(i, targetType, ret, manager, file, depTrs, isOnTheFly, pkgXml, true);
                continue;
            }
            if (coveredTypes.contains(DependencyType.BUILDTOOL)) {
                if (firstBuildtoolDependency == null) {
                    firstBuildtoolDependency = i;
                } else {
                    buildtoolWarningRaised = true;
                    raiseWarning(i, targetType, ret, manager, file, depTrs, isOnTheFly, pkgXml, false);
                }
            }
        }
        if (buildtoolWarningRaised) {
            raiseWarning(firstBuildtoolDependency, DependencyType.BUILDTOOL, ret, manager, file, depTrs,
                    isOnTheFly, pkgXml, false);
        }
        return ret.toArray(ProblemDescriptor.EMPTY_ARRAY);
    }

    private void raiseWarning(int i, DependencyType targetType, @NotNull List<ProblemDescriptor> ret,
                              @NotNull InspectionManager manager,
                              PsiFile file, @NotNull List<Pair<TextRange, TextRange>> depTrs,
                              boolean isOnTheFly, ROSPackageXml pkgXml, boolean typeNotAllowed) {
        ret.add(manager.createProblemDescriptor(file, depTrs.get(i).second, getMessage(targetType, typeNotAllowed),
                ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly,
                new RemoveDependencyQuickFix(pkgXml, i)));
    }

    @NotNull
    @Contract(pure = true)
    private String getMessage(DependencyType targetType, boolean typeNotAllowed) {
        return typeNotAllowed ? "Metapackages may not have any " + targetType.name().toLowerCase() + " dependencies."
                : "Metapackages may have at most ONE " + DependencyType.BUILDTOOL.name().toLowerCase() + " dependency.";
    }
}
