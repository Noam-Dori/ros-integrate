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
import ros.integrate.pkg.xml.ExportTag;
import ros.integrate.pkg.xml.PackageXmlUtil;
import ros.integrate.pkg.xml.ROSPackageXml;
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
        List<ROSPackageXml.Dependency> dependencies = pkgXml.getDependencies(null);
        List<Pair<TextRange, TextRange>> depTrs = pkgXml.getDependencyTextRanges();
        List<ProblemDescriptor> ret = new ArrayList<>();
        for (int i = dependencies.size() - 1; i >= 0; i--) {
            ROSPackageXml target = dependencies.get(i).getPackage().getPackageXml();
            int id = i;
            Optional.ofNullable(target).map(ROSPackageXml::getExport).map(ExportTag::deprecatedMessage)
                    .ifPresent(msg -> ret.add(manager.createProblemDescriptor(file, depTrs.get(id).second,
                            getMessage(target.getPackage().getName(), msg),
                            ProblemHighlightType.LIKE_DEPRECATED, isOnTheFly,
                            new RemoveDependencyQuickFix(pkgXml, id))));
        }
        return ret.toArray(ProblemDescriptor.EMPTY_ARRAY);
    }

    @NotNull
    @Contract(pure = true)
    private String getMessage(@NotNull String pkgName, @Nullable String deprecatedMessage) {
        return "Package " + pkgName + " is deprecated" + (deprecatedMessage == null ? "." : ": " +
                deprecatedMessage.replaceAll("^\n", ""));
    }
}
