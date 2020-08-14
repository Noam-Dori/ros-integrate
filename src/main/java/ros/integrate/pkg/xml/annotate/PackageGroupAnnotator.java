package ros.integrate.pkg.xml.annotate;

import com.intellij.codeInsight.daemon.impl.analysis.RemoveTagIntentionFix;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.PackageXmlUtil;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.ROSPackageXml.GroupLink;
import ros.integrate.pkg.xml.TagTextRange;
import ros.integrate.pkg.xml.condition.highlight.ROSConditionSyntaxHighlighter;
import ros.integrate.pkg.xml.intention.ReformatPackageXmlFix;

import java.util.List;

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

    public PackageGroupAnnotator(@NotNull ROSPackageXml pkgXml, @NotNull AnnotationHolder holder) {
        this.holder = holder;
        this.pkgXml = pkgXml;
        groupDepends = pkgXml.getGroupDepends();
        groups = pkgXml.getGroups();
        groupDependTrs = pkgXml.getGroupDependTextRanges();
        groupTrs = pkgXml.getGroupTextRanges();
        format = pkgXml.getFormat();
    }

    public void annTooLowFormat() {
        if (format >= 3) {
            return;
        }
        XmlTag[] tags = pkgXml.findSubTags("member_of_group");
        for (int i = 0; i < groups.size(); i++) {
            Annotation ann = holder.createErrorAnnotation(groupTrs.get(i),
                    "member_of_group is supported from format 3");
            ann.registerFix(new ReformatPackageXmlFix(pkgXml, true));
            ann.registerFix(new RemoveTagIntentionFix("member_of_group", tags[i]));
        }
        tags = pkgXml.findSubTags("group_depend");
        for (int i = 0; i < groupDepends.size(); i++) {
            Annotation ann = holder.createErrorAnnotation(groupDependTrs.get(i),
                    "group_depend is supported from format 3");
            ann.registerFix(new ReformatPackageXmlFix(pkgXml, true));
            ann.registerFix(new RemoveTagIntentionFix("group_depend", tags[i]));
        }
    }

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
}
