package ros.integrate.pkg.xml.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.xml.ExportTag;
import ros.integrate.pkg.xml.PackageXmlUtil;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.TagTextRange;
import ros.integrate.pkg.xml.intention.RemoveDependencyQuickFix;

import java.util.*;

public class DeprecatedPackageInspection extends LocalInspectionTool {

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
        for (int i = dependencies.size() - 1; i >= 0; i--) {
            ROSPackageXml.Dependency dep = dependencies.get(i);
            if (PackageXmlUtil.conditionEvaluatesToFalse(dep, format)) {
                continue;
            }
            ROSPackageXml target = dep.getPackage().getPackageXml();
            int id = i;
            Optional.ofNullable(target).map(ROSPackageXml::getExport).map(ExportTag::deprecatedMessage)
                    .ifPresent(msg -> ret.add(manager.createProblemDescriptor(file, depTrs.get(id).value(),
                            getMessage(target.getPackage().getName(), msg),
                            ProblemHighlightType.LIKE_DEPRECATED, isOnTheFly,
                            new RemoveDependencyQuickFix(pkgXml, id))));
        }
        return ret.toArray(ProblemDescriptor.EMPTY_ARRAY);
    }

    @NotNull
    @Contract(pure = true)
    private String getMessage(@NotNull String pkgName, @NotNull String deprecatedMessage) {
        return "Package " + pkgName + " is deprecated" + (deprecatedMessage.isEmpty() ? "." : ": " +
                deprecatedMessage.replaceAll("^\n", ""));
    }
}
