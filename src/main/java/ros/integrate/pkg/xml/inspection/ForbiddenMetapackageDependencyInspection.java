package ros.integrate.pkg.xml.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.xml.DependencyType;
import ros.integrate.pkg.xml.PackageXmlUtil;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.TagTextRange;
import ros.integrate.pkg.xml.intention.RemoveDependencyQuickFix;

import java.util.*;

/**
 * <p>Checks whether or not the metapackage uses illegal dependencies.</p>
 * <p>According to <a href="https://www.ros.org/reps/rep-0140.html#metapackage">the ROS standards</a>,
 *     metapackages only provide execution-time dependencies, cannot be used for catkin builds and compile nothing themselves.
 *     <br/>
 *     The only thing they are allowed to depend on is ONE buildtool dependency which is used to mark the package as a metapackage.
 * </p>
 * <p>these dependency tags will be annotated in meta-packages as forbidden:</p>
 * <ul>
 *     <li><code>depend</code></li>
 *     <li><code>build_depend</code></li>
 *     <li><code>build_export_depend</code></li>
 *     <li><code>buildtool_export_depend</code></li>
 *     <li><code>test_depend</code></li>
 *     <li><code>doc_depend</code></li>
 *     <li><code>conflict</code></li>
 *     <li><code>replace</code></li>
 *     <li><code>run_depend</code></li>
 * </ul>
 * <p>these dependency tags are allowed, at least to some extent:</p>
 * <ul>
 *     <li><code>exec_depend</code></li>
 *     <li><code>buildtool_depend</code>, but only once</li>
 * </ul>
 * <p>this inspection offers one fix:</p>
 * <ol>
 *     <li>Remove the dependency tag(s).</li>
 * </ol>
 * @author Noam Dori
 */
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
        List<TagTextRange> depTrs = pkgXml.getDependencyTextRanges();
        List<ProblemDescriptor> ret = new ArrayList<>();
        Integer firstBuildtoolDependency = null;
        boolean buildtoolWarningRaised = false;
        for (int i = dependencies.size() - 1; i >= 0; i--) {
            DependencyType targetType = dependencies.get(i).getType();
            List<DependencyType> coveredTypes = Arrays.asList(targetType.getCoveredDependencies());
            if (!new HashSet<>(ALLOWED_DEP_TYPES).containsAll(coveredTypes)) {
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
                              PsiFile file, @NotNull List<TagTextRange> depTrs,
                              boolean isOnTheFly, ROSPackageXml pkgXml, boolean typeNotAllowed) {
        ret.add(manager.createProblemDescriptor(file, depTrs.get(i).name(), getMessage(targetType, typeNotAllowed),
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
