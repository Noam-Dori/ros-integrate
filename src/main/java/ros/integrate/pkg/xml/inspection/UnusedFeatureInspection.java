package ros.integrate.pkg.xml.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.xml.ExportTag;
import ros.integrate.pkg.xml.PackageXmlUtil;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.TagTextRange;
import ros.integrate.pkg.xml.intention.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <p>reports used features that do not exist in the current format</p>
 * <p>For example, if a ROS condition attribute is found in a format 2 package.xml file,
 *     the entire attribute will be annotated as unused.</p>
 * <p>this inspection offers two fixes per feature:</p>
 * <ol>
 *     <li>Update the package to use the latest format. This will also reformat the package and order its contents
 *         correctly</li>
 *     <li>Remove the feature from the tag (or if the feature is a tag, remove the tag)</li>
 * </ol>
 * @author Noam Dori
 */
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
                        "License file reference is only used from format 3",
                        ProblemHighlightType.LIKE_UNUSED_SYMBOL, isOnTheFly,
                        new ReformatPackageXmlFix(pkgXml, true),
                        new RemoveLicenseFileFix(pkgXml, i)));
            }
        }
        List<ROSPackageXml.Dependency> dependencies = pkgXml.getDependencies(null);
        List<TagTextRange> dependencyTrs = pkgXml.getDependencyTextRanges();
        for (int i = 0; i < dependencies.size(); i++) {
            ROSPackageXml.Dependency dependency = dependencies.get(i);
            if (format < 3 && dependency.getCondition() != null) {
                ret.add(manager.createProblemDescriptor(file, dependencyTrs.get(i).attr("condition"),
                        "Conditions are only used from format 3",
                        ProblemHighlightType.LIKE_UNUSED_SYMBOL, isOnTheFly,
                        new ReformatPackageXmlFix(pkgXml, true),
                        new RemoveDependencyConditionFix(pkgXml, i)));
            }
        }
        ExportTag export = pkgXml.getExport();
        Optional.ofNullable(export).map(ExportTag::getBuildTypes).ifPresent(buildTypes -> {
            if (format >= 3) {
                return;
            }
            for (int i = 0; i < buildTypes.size(); i++) {
                if (buildTypes.get(i).getCondition() != null) {
                    ret.add(manager.createProblemDescriptor(file, export.getBuildTypeTextRanges().get(i)
                                    .attr("condition"),
                            "Conditions are only used from format 3",
                            ProblemHighlightType.LIKE_UNUSED_SYMBOL, isOnTheFly,
                            new ReformatPackageXmlFix(pkgXml, true),
                            new RemoveBuildTypeConditionFix(export, i)));
                }
            }
        });
        return ret.toArray(ProblemDescriptor.EMPTY_ARRAY);
    }
}
