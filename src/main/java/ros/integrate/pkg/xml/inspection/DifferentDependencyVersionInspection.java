package ros.integrate.pkg.xml.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.pkg.xml.PackageXmlUtil;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.TagTextRange;
import ros.integrate.pkg.xml.intention.ReformatPackageXmlFix;
import ros.integrate.pkg.xml.intention.RemoveDependencyQuickFix;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>Checks if there are multiple dependency tags that point to the same package
 *     but with different version restrictions.</p>
 * <p>Consider this package:</p>
 * <code>
 *     &lt;package&gt;<br/>
 *     &nbsp;&nbsp;&nbsp;&nbsp&lt;depend&gt;catkin&lt;/depend&gt;<br/>
 *     &nbsp;&nbsp;&nbsp;&nbsp&lt;test_depend version_gte="1.0.0"&gt;catkin&lt;/depend&gt;<br/>
 *     &lt;/package&gt;
 * </code>
 * <p>This package has two dependencies that require somewhat different versions:
 *     the first can work with any version of <code>catkin</code>,
 *     but the second requires <code>catkin</code> to be at least version 1.0.0.
 * </p>
 * <p>For this reason, the values of both dependency tags will be annotated.</p>
 * <p>This inspection offers two fixes:</p>
 * <ol>
 *     <li>Reformat the entire file. This will also change the version restriction to match the versions common to all tags.
 *     If no such common version exists, the dependencies will be removed.</li>
 *     <li>Remove the dependency tag(s).</li>
 * </ol>
 * @author Noam Dori
 */
public class DifferentDependencyVersionInspection extends LocalInspectionTool {
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
            ROSPackageXml.Dependency di = dependencies.get(i), dj;
            if (di.getPackage() == ROSPackage.ORPHAN || PackageXmlUtil.conditionEvaluatesToFalse(di, format)) {
                continue;
            }
            boolean found = false;
            for (int j = i - 1; j >= 0; j--) {
                dj = dependencies.get(j);
                if (PackageXmlUtil.conditionEvaluatesToFalse(dj, format)) {
                    continue;
                }
                if (dj.getPackage().equals(di.getPackage()) && !dj.getVersionRange().equals(di.getVersionRange())) {
                    trsToAnn.add(j);
                    found = true;
                }
            }
            if (found) {
                trsToAnn.add(i);
            }
        }
        trsToAnn.forEach(i -> ret.add(manager.createProblemDescriptor(file, depTrs.get(i).value(), getDisplayName(),
                ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly,
                new ReformatPackageXmlFix(pkgXml, false),
                new RemoveDependencyQuickFix(pkgXml, i))));
        return ret.toArray(ProblemDescriptor.EMPTY_ARRAY);
    }
}
