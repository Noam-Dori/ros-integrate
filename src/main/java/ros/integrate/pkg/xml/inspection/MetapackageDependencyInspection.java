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
import ros.integrate.pkg.xml.intention.RemoveDependencyQuickFix;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <p>Checks if normal (concrete) packages depend on metapackages.</p>
 * <p>According to <a href="https://www.ros.org/reps/rep-0140.html#metapackage">the ROS standards</a>,
 *     only metapackages may depend on other metapackages. Normal, or non-meta packages may not depend
 *     on metapackages in any way.
 * </p>
 * <p>Consider packages <b>core</b> and <b>leaf</b>:</p>
 * <code>
 *     &lt;package&gt;<br/>
 *     &nbsp;&nbsp;&nbsp;&nbsp;&lt;name&gt;core&lt;/name&gt;<br/>
 *     &nbsp;&nbsp;&nbsp;&nbsp;&lt;export&gt;<br/>
 *     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;metapackage/&gt;<br/>
 *     &nbsp;&nbsp;&nbsp;&nbsp;&lt;/export&gt;<br/>
 *     &lt;/package&gt;<br/>
 *     <br/>
 *     &lt;package&gt;<br/>
 *     &nbsp;&nbsp;&nbsp;&nbsp;&lt;name&gt;leaf&lt;/name&gt;<br/>
 *     &nbsp;&nbsp;&nbsp;&nbsp;&lt;depend&gt;core&lt;/depend&gt;<br/>
 *     &lt;/package&gt;
 * </code>
 * <p>the leaf package clearly depends on the metapackage "core", so the <code>depend</code> tag will be annotated.</p>
 * <p>this inspection offers one fix:</p>
 * <ol>
 *     <li>Remove the dependency tag(s).</li>
 * </ol>
 * @author Noam Dori
 */
public class MetapackageDependencyInspection extends LocalInspectionTool {
    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        ROSPackageXml pkgXml = PackageXmlUtil.getWrapper(file);
        if (pkgXml == null || Optional.ofNullable(pkgXml.getExport()).map(ExportTag::isMetapackage).orElse(false)) {
            return null;
        }
        List<ROSPackageXml.Dependency> dependencies = pkgXml.getDependencies(null);
        List<TagTextRange> depTrs = pkgXml.getDependencyTextRanges();
        List<ProblemDescriptor> ret = new ArrayList<>();
        for (int i = dependencies.size() - 1; i >= 0; i--) {
            ROSPackageXml target = dependencies.get(i).getPackage().getPackageXml();
            if (Optional.ofNullable(target).map(ROSPackageXml::getExport)
                    .map(ExportTag::isMetapackage).orElse(false)) {
                ret.add(manager.createProblemDescriptor(file, depTrs.get(i).value(), getDisplayName(),
                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly,
                        new RemoveDependencyQuickFix(pkgXml, i)));
            }
        }
        return ret.toArray(ProblemDescriptor.EMPTY_ARRAY);
    }
}
