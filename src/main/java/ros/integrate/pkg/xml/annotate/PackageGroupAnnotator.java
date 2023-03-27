package ros.integrate.pkg.xml.annotate;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.PackageXmlUtil;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.ROSPackageXml.GroupLink;
import ros.integrate.pkg.xml.TagTextRange;
import ros.integrate.pkg.xml.condition.highlight.ROSConditionSyntaxHighlighter;
import ros.integrate.pkg.xml.intention.ReformatPackageXmlFix;
import ros.integrate.pkg.xml.intention.RemoveGroupTagQuickFix;

import java.util.*;

/**
 * A facade class used to annotate package.xml files for anything related to the group tags (group_depend,
 * member_of_group)
 * @author Noam Dori
 */
public class PackageGroupAnnotator {
    @NotNull
    private final ROSPackageXml pkgXml;
    @NotNull
    private final AnnotationHolder holder;
    private final int format;
    @NotNull
    private final List<GroupLink> groupDepends, groups;
    @NotNull
    private final List<TagTextRange> groupDependTrs, groupTrs;

    /**
     * construct the annotator
     *
     * @param pkgXml the reference package.xml file
     * @param holder the annotation holder.
     */
    public PackageGroupAnnotator(@NotNull ROSPackageXml pkgXml, @NotNull AnnotationHolder holder) {
        this.holder = holder;
        this.pkgXml = pkgXml;
        groupDepends = pkgXml.getGroupDepends();
        groups = pkgXml.getGroups();
        groupDependTrs = pkgXml.getGroupDependTextRanges();
        groupTrs = pkgXml.getGroupTextRanges();
        format = pkgXml.getFormat();
    }

    /**
     * annotates tags if they exist in a package with a format lower than 3
     */
    public void annTooLowFormat() {
        if (format >= 3) {
            return;
        }
        for (int i = 0; i < groups.size(); i++) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Member_of_group is supported from format 3")
                    .range(groupTrs.get(i))
                    .withFix(new ReformatPackageXmlFix(pkgXml, true))
                    .withFix(new RemoveGroupTagQuickFix(pkgXml, i, false))
                    .create();
        }
        for (int i = 0; i < groupDepends.size(); i++) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Group_depend is supported from format 3")
                    .range(groupDependTrs.get(i))
                    .withFix(new ReformatPackageXmlFix(pkgXml, true))
                    .withFix(new RemoveGroupTagQuickFix(pkgXml, i, true))
                    .create();
        }
    }

    /**
     * annotates tags that contains a false evaluating condition.
     */
    public void annIgnoredCondition() {
        if (format < 3) {
            return;
        }
        for (int i = 0; i < groupDepends.size(); i++) {
            if (PackageXmlUtil.conditionEvaluatesToFalse(groupDepends.get(i), format)) {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(groupDependTrs.get(i))
                        .textAttributes(ROSConditionSyntaxHighlighter.IGNORED)
                        .create();
            }
        }
        for (int i = 0; i < groups.size(); i++) {
            if (PackageXmlUtil.conditionEvaluatesToFalse(groups.get(i), format)) {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(groupTrs.get(i))
                        .textAttributes(ROSConditionSyntaxHighlighter.IGNORED)
                        .create();
            }
        }
    }

    /**
     * annotates group tags that have no value
     */
    public void annEmptyGroup() {
        for (int i = 0; i < groups.size(); i++) {
            if (groupTrs.get(i).value() == groupTrs.get(i)) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Empty group tag.")
                        .range(groupTrs.get(i).name())
                        .withFix(new RemoveGroupTagQuickFix(pkgXml, i, false))
                        .create();
            }
        }
        for (int i = 0; i < groupDepends.size(); i++) {
            if (groupDependTrs.get(i).value() == groupDependTrs.get(i)) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Empty group tag.")
                        .range(groupDependTrs.get(i).name())
                        .withFix(new RemoveGroupTagQuickFix(pkgXml, i, true))
                        .create();
            }
        }
    }

    /**
     * annotates tags that point to the same group. This annotation respects conditions and acts on group_depend and
     * member_of_group separately
     */
    public void annConflictingGroups() {
        annConflictAny(groups, groupTrs, false);
        annConflictAny(groupDepends, groupDependTrs, true);
    }

    private void annConflictAny(@NotNull List<GroupLink> groupLinks, @NotNull List<TagTextRange> textRanges,
                                boolean isDependency) {
        Set<Integer> trsToAnn = new HashSet<>();
        for (int i = groupLinks.size() - 1; i >= 0; i--) {
            GroupLink gi = groupLinks.get(i), gj;
            if (gi.getGroup().isEmpty()) {
                continue;
            }
            boolean found = false;
            for (int j = i - 1; j >= 0; j--) {
                gj = groupLinks.get(j);
                if (gj.getGroup().equals(gi.getGroup()) && PackageXmlUtil.mayConflict(gi, gj, format)) {
                    trsToAnn.add(j);
                    found = true;
                }
            }
            if (found) {
                trsToAnn.add(i);
            }
        }
        trsToAnn.forEach(i -> holder.newAnnotation(HighlightSeverity.ERROR, "Group tag conflicts with another tag in the file.")
                .range(textRanges.get(i).value())
                .withFix(new RemoveGroupTagQuickFix(pkgXml, i, isDependency))
                .create());
    }
}
