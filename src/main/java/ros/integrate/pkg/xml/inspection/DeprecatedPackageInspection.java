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

/**
 * <p>checks whether or not the package depends on any deprecated packages, and marks it accordingly.</p>
 * <p>Consider packages <b>core</b> and <b>leaf</b>:</p>
 * <code>
 *     &lt;package&gt;<br/>
 *     &nbsp;&nbsp;&nbsp;&nbsp;&lt;name&gt;core&lt;/name&gt;<br/>
 *     &nbsp;&nbsp;&nbsp;&nbsp;&lt;export&gt;<br/>
 *     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;deprecated/&gt;<br/>
 *     &nbsp;&nbsp;&nbsp;&nbsp;&lt;/export&gt;<br/>
 *     &lt;/package&gt;<br/>
 *     <br/>
 *     &lt;package&gt;<br/>
 *     &nbsp;&nbsp;&nbsp;&nbsp;&lt;name&gt;leaf&lt;/name&gt;<br/>
 *     &nbsp;&nbsp;&nbsp;&nbsp;&lt;depend&gt;core&lt;/depend&gt;<br/>
 *     &lt;/package&gt;
 * </code>
 * <p>the leaf package clearly depends on a deprecated package here, so the <code>depend</code> tag will be annotated with
 *     the deprecation message.</p>
 * <p>Note that this only uses the deprecated annotation and does not use the information from the dependency versions.
 *     This job is left to another inspection.</p>
 * <p>this inspection offers one fix:</p>
 * <ol>
 *     <li>Remove the dependency tag(s).</li>
 * </ol>
 * @author Noam Dori
 */
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
