package ros.integrate.pkg.xml.annotate;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationBuilder;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import ros.integrate.pkg.xml.PackageXmlUtil;
import ros.integrate.pkg.xml.TagTextRange;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.ROSDepKeyCache;
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.pkg.xml.DependencyType;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.ROSPackageXml.Dependency;
import ros.integrate.pkg.xml.condition.highlight.ROSConditionSyntaxHighlighter;
import ros.integrate.pkg.xml.intention.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A facade class used to annotate package.xml files for anything related to the dependency type tags (depend,
 * test_depend, etc.).
 */
class PackageDependencyAnnotator {
    @NotNull
    private final ROSPackageXml pkgXml;
    @NotNull
    private final AnnotationHolder holder;

    @NotNull
    private final List<Dependency> dependencies;

    @NotNull
    private final List<TagTextRange> depTrs;

    private final int format;

    /**
     * construct the annotator
     *
     * @param pkgXml the reference package.xml file
     * @param holder the annotation holder.
     */
    @Contract(pure = true)
    PackageDependencyAnnotator(@NotNull ROSPackageXml pkgXml, @NotNull AnnotationHolder holder) {
        this.holder = holder;
        this.pkgXml = pkgXml;
        dependencies = pkgXml.getDependencies(null);
        depTrs = pkgXml.getDependencyTextRanges();
        format = pkgXml.getFormat();
    }

    /**
     * annotates tags that point to themselves
     */
    void annSelfDependency() {
        for (int i = 0; i < dependencies.size(); i++) {
            if (pkgXml.getPackage().equals(dependencies.get(i).getPackage())) {
                holder.newAnnotation(HighlightSeverity.ERROR, "A package cannot depend on itself.")
                        .range(depTrs.get(i).value())
                        .withFix(new RemoveDependencyQuickFix(pkgXml, i))
                        .create();
            }
        }
    }

    /**
     * annotates tags that are not allowed to be used the the manifest's current format.
     * For example, run_depend is only allowed in format 1 manifests
     */
    void annInvalidDependencyName() {
        // get invalid tag names
        List<String> relevant = Arrays.stream(DependencyType.values())
                .filter(dep -> !dep.relevant(pkgXml.getFormat())).map(DependencyType::getTagName)
                .collect(Collectors.toList());
        for (int i = 0; i < dependencies.size(); i++) {
            String tagName = dependencies.get(i).getType().getTagName();
            if (relevant.contains(tagName)) {
                holder.newAnnotation(HighlightSeverity.ERROR,
                        "Dependency tag " + tagName + " may not be used in manifest format " +
                                pkgXml.getFormat() + ".")
                        .range(depTrs.get(i).name())
                        .withFix(new RemoveDependencyQuickFix(pkgXml, i))
                        .withFix(new ReformatPackageXmlFix(pkgXml, false))
                        .create();
            }
        }
    }

    /**
     * annotates empty dependency tags
     */
    void annEmptyDependency() {
        for (int i = 0; i < dependencies.size(); i++) {
            if (depTrs.get(i).value() == depTrs.get(i)) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Empty dependency tag.")
                        .range(depTrs.get(i).name())
                        .withFix(new RemoveDependencyQuickFix(pkgXml, i))
                        .create();
            }
        }
    }

    /**
     * annotates tags that are conflicting. For example, depend and exec_depend cannot be used together on the same
     * dependency because both declare an "execution time dependency". This annotation respects conditions.
     */
    void annConflictingDependencies() {
        Set<Integer> trsToAnn = new HashSet<>();
        for (int i = dependencies.size() - 1; i >= 0; i--) {
            Dependency di = dependencies.get(i), dj;
            if (di.getPackage() == ROSPackage.ORPHAN) {
                continue;
            }
            boolean found = false;
            for (int j = i - 1; j >= 0; j--) {
                dj = dependencies.get(j);
                Set<DependencyType> allCovered = new HashSet<>(Arrays.asList(dj.getType().getCoveredDependencies()));
                allCovered.retainAll(Arrays.asList(di.getType().getCoveredDependencies()));
                if (dj.getPackage().equals(di.getPackage()) && !allCovered.isEmpty() &&
                        PackageXmlUtil.mayConflict(di, dj, format)) {
                    trsToAnn.add(j);
                    found = true;
                }
            }
            if (found) {
                trsToAnn.add(i);
            }
        }
        trsToAnn.forEach(i -> holder.newAnnotation(HighlightSeverity.ERROR, "Dependency Tag conflicts with another tag in the file.")
                .range(depTrs.get(i).value())
                .withFix(new RemoveDependencyQuickFix(pkgXml, i))
                .withFix(new ReformatPackageXmlFix(pkgXml, false))
                .create());
    }

    /**
     * annotates tags that point to an unknown package or dependency.
     */
    void annDependencyNotFound() {
        for (int i = 0; i < dependencies.size(); i++) {
            Dependency dep = dependencies.get(i);
            if (PackageXmlUtil.conditionEvaluatesToFalse(dep, format)) {
                continue;
            }
            if (dep.getPackage() == ROSPackage.ORPHAN
                    && depTrs.get(i).value() != depTrs.get(i)) {
                AnnotationBuilder ann = holder.newAnnotation(HighlightSeverity.ERROR, "Unresolved dependency")
                        .range(depTrs.get(i).value())
                        .highlightType(ProblemHighlightType.ERROR)
                        .withFix(new RemoveDependencyQuickFix(pkgXml, i));
                if (pkgXml.getPackage().getProject().getService(ROSDepKeyCache.class).inOfflineMode()) {
                    ann = ann.withFix(new ForceCacheQuickFix());
                }
                ann.create();
            }
        }
    }

    /**
     * annotates attributes of tags that result in an invalid version range. For example,
     * {@code version_lt=1.0.0} and {@code version_gt=2.0.0} means no version is possible and should be annotated.
     */
    void annInvalidDependencyVersionAttr() {
        for (int i = 0; i < dependencies.size(); i++) {
            Dependency dep = dependencies.get(i);
            if (dep.getPackage() != ROSPackage.ORPHAN && dep.getVersionRange().isNotValid()) {
                for (TextRange tr : depTrs.get(i).attrQuery(null,
                        "version_lt", "version_lte", "version_gt", "version_gte", "version_eq")) {
                    holder.newAnnotation(HighlightSeverity.ERROR, "Invalid version restriction(s).")
                            .range(tr)
                            .withFix(new AmputateDependencyQuickFix(pkgXml, i))
                            .withFix(new FixDependencyQuickFix(pkgXml, i, false))
                            .create();
                }
            }
        }
    }

    /**
     * annotates version attributes that cannot be together in the same tag, yet are. For example,
     * version_lt and version_lte should not be together in the same tag.
     */
    void annConflictingVersionAttr() {
        if (dependencies.size() == 0) {
            return;
        }
        Stream<XmlTag> result = Stream.empty();
        for (DependencyType dep : DependencyType.values()) {
            result = Stream.concat(result, Stream.of(Objects.requireNonNull(pkgXml.getRawXml().getRootTag())
                    .findSubTags(dep.getTagName())));
        }
        List<XmlTag> rawDependencies = result.collect(Collectors.toList());
        for (int i = 0; i < rawDependencies.size(); i++) {
            XmlTag dep = rawDependencies.get(i);
            boolean hasLt = dep.getAttribute("version_lt") != null,
                    hasLte = dep.getAttribute("version_lte") != null,
                    hasGt = dep.getAttribute("version_gt") != null,
                    hasGte = dep.getAttribute("version_gte") != null,
                    hasEq = dep.getAttribute("version_eq") != null;
            List<TextRange> raised = new ArrayList<>(4);
            TagTextRange queryable = depTrs.get(i);
            if (hasEq && (hasGt || hasGte || hasLt || hasLte)) {
                raised = queryable.attrQuery(TagTextRange.Prefix.NAME,
                        "version_lt", "version_lte", "version_gt", "version_gte", "version_eq");
            } else {
                if (hasGt && hasGte) {
                    raised.add(queryable.attrName("version_gt"));
                    raised.add(queryable.attrName("version_gte"));
                }
                if (hasLt && hasLte) {
                    raised.add(queryable.attrName("version_lt"));
                    raised.add(queryable.attrName("version_lte"));
                }
            }
            if (raised.size() > 1) {
                for (TextRange tr : raised) {
                    holder.newAnnotation(HighlightSeverity.ERROR, "Conflicting version restrictions.")
                            .range(tr)
                            .withFix(new AmputateDependencyQuickFix(pkgXml, i))
                            .withFix(new FixDependencyQuickFix(pkgXml, i, false))
                            .create();
                }
            }
        }
    }

    /**
     * annotates tags that contains a false evaluating condition.
     */
    void annIgnoredCondition() {
        for (int i = 0; i < dependencies.size(); i++) {
            if (PackageXmlUtil.conditionEvaluatesToFalse(dependencies.get(i), format)) {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(depTrs.get(i))
                        .textAttributes(ROSConditionSyntaxHighlighter.IGNORED)
                        .create();
            }
        }
    }

    /**
     * annotates packages that no no buildtool_depend tags.
     */
    public void annNoBuildtoolDependency() {
        if (pkgXml.getDependencies(DependencyType.BUILDTOOL).isEmpty()) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Package must include at least one buildtool dependency.")
                    .range(pkgXml.getRootTextRange())
                    .withFix(new AddBuildtoolDependencyFix(pkgXml))
                    .create();
        }
    }
}
