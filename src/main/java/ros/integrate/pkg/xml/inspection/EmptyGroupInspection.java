package ros.integrate.pkg.xml.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.ROSPackageManager;
import ros.integrate.pkg.xml.PackageXmlUtil;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.TagTextRange;
import ros.integrate.pkg.xml.intention.RemoveGroupTagQuickFix;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Checks if a group dependency points to a group that has no members</p>
 * <p>consider the following package:</p>
 * <code>
 *     &lt;package&gt;<br/>
 *     &nbsp;&nbsp;&nbsp;&nbsp;&lt;group_depend&gt;new_group&lt;/group_depend&gt;<br/>
 *     &lt;/package&gt;<br/>
 * </code>
 * <p>since this group name was never used, no other package is pointing to this group using a <code>member_of_group</code>
 *     tag. Therefore, the <code>group_depend</code> tag is annotated.</p>
 * <p>this inspection offers one fix:</p>
 * <ol>
 *     <li>Remove the group_depend tag.</li>
 * </ol>
 * @author Noam Dori
 */
public class EmptyGroupInspection extends LocalInspectionTool {
    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        ROSPackageXml pkgXml = PackageXmlUtil.getWrapper(file);
        if (pkgXml == null) {
            return null;
        }
        int format = pkgXml.getFormat();
        if (format < 3) {
            return null;
        }
        List<ProblemDescriptor> ret = new ArrayList<>();
        List<ROSPackageXml.GroupLink> groups = pkgXml.getGroupDepends();
        List<TagTextRange> groupTrs = pkgXml.getGroupDependTextRanges();
        ROSPackageManager pkgManager = file.getProject().getService(ROSPackageManager.class);
        for (int i = groups.size() - 1; i >= 0; i--) {
            ROSPackageXml.GroupLink group = groups.get(i);
            if (PackageXmlUtil.conditionEvaluatesToFalse(group, format)) {
                continue;
            }
            if (pkgManager.findGroupMembers(group.getGroup()).isEmpty()) {
                ret.add(manager.createProblemDescriptor(file, groupTrs.get(i).value(),
                        getDisplayName(), ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly,
                        new RemoveGroupTagQuickFix(pkgXml, i, true)));
            }
        }
        return ret.toArray(ProblemDescriptor.EMPTY_ARRAY);
    }
}
