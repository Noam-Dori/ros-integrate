package ros.integrate.pkg.xml.annotate;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
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
            Annotation ann = holder.createErrorAnnotation(groupTrs.get(i),
                    "member_of_group is supported from format 3");
            ann.registerFix(new ReformatPackageXmlFix(pkgXml, true));
            ann.registerFix(new RemoveGroupTagQuickFix(pkgXml, i, false));
        }
        for (int i = 0; i < groupDepends.size(); i++) {
            Annotation ann = holder.createErrorAnnotation(groupDependTrs.get(i),
                    "group_depend is supported from format 3");
            ann.registerFix(new ReformatPackageXmlFix(pkgXml, true));
            ann.registerFix(new RemoveGroupTagQuickFix(pkgXml, i, true));
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
                Annotation ann = holder.createInfoAnnotation(groupDependTrs.get(i), null);
                ann.setTextAttributes(ROSConditionSyntaxHighlighter.IGNORED);
            }
        }
        for (int i = 0; i < groups.size(); i++) {
            if (PackageXmlUtil.conditionEvaluatesToFalse(groups.get(i), format)) {
                Annotation ann = holder.createInfoAnnotation(groupTrs.get(i), null);
                ann.setTextAttributes(ROSConditionSyntaxHighlighter.IGNORED);
            }
        }
    }

    /**
     * annotates group tags that have no value
     */
    public void annEmptyGroup() {
        for (int i = 0; i < groups.size(); i++) {
            if (groupTrs.get(i).value() == groupTrs.get(i)) {
                Annotation ann = holder.createErrorAnnotation(groupTrs.get(i).name(),
                        "Empty group tag.");
                ann.registerFix(new RemoveGroupTagQuickFix(pkgXml, i, false));
            }
        }
        for (int i = 0; i < groupDepends.size(); i++) {
            if (groupDependTrs.get(i).value() == groupDependTrs.get(i)) {
                Annotation ann = holder.createErrorAnnotation(groupDependTrs.get(i).name(),
                        "Empty group tag.");
                ann.registerFix(new RemoveGroupTagQuickFix(pkgXml, i, true));
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
        trsToAnn.forEach(i -> {
            Annotation ann = holder.createErrorAnnotation(textRanges.get(i).value(),
                    "Group tag conflicts with another tag in the file.");
            ann.registerFix(new RemoveGroupTagQuickFix(pkgXml, i, isDependency));
        });
    }
}
