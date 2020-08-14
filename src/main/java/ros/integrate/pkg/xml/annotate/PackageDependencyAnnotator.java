package ros.integrate.pkg.xml.annotate;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
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

    @Contract(pure = true)
    PackageDependencyAnnotator(@NotNull ROSPackageXml pkgXml, @NotNull AnnotationHolder holder) {
        this.holder = holder;
        this.pkgXml = pkgXml;
        dependencies = pkgXml.getDependencies(null);
        depTrs = pkgXml.getDependencyTextRanges();
        format = pkgXml.getFormat();
    }

    void annSelfDependency() {
        for (int i = 0; i < dependencies.size(); i++) {
            if (pkgXml.getPackage().equals(dependencies.get(i).getPackage())) {
                Annotation ann = holder.createErrorAnnotation(depTrs.get(i).value(),
                        "A package cannot depend on itself.");
                ann.registerFix(new RemoveDependencyQuickFix(pkgXml, i));
            }
        }
    }

    void annInvalidDependencyName() {
        // get invalid tag names
        List<String> relevant = Arrays.stream(DependencyType.values())
                .filter(dep -> !dep.relevant(pkgXml.getFormat())).map(DependencyType::getTagName)
                .collect(Collectors.toList());
        for (int i = 0; i < dependencies.size(); i++) {
            String tagName = dependencies.get(i).getType().getTagName();
            if (relevant.contains(tagName)) {
                Annotation ann = holder.createErrorAnnotation(depTrs.get(i).name(),
                        "Dependency tag " + tagName + " may not be used in manifest format " +
                                pkgXml.getFormat() + ".");
                ann.registerFix(new RemoveDependencyQuickFix(pkgXml, i));
                ann.registerFix(new ReformatPackageXmlFix(pkgXml, false));
            }
        }
    }

    void annEmptyDependency() {
        for (int i = 0; i < dependencies.size(); i++) {
            if (depTrs.get(i).value() == depTrs.get(i)) {
                Annotation ann = holder.createErrorAnnotation(depTrs.get(i).name(),
                        "Empty dependency tag.");
                ann.registerFix(new RemoveDependencyQuickFix(pkgXml, i));
            }
        }
    }

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
        trsToAnn.forEach(i -> {
            Annotation ann = holder.createErrorAnnotation(depTrs.get(i).value(),
                    "Dependency Tag conflicts with another tag in the file.");
            ann.registerFix(new RemoveDependencyQuickFix(pkgXml, i));
            ann.registerFix(new ReformatPackageXmlFix(pkgXml, false));
        });
    }

    void annDependencyNotFound() {
        for (int i = 0; i < dependencies.size(); i++) {
            Dependency dep = dependencies.get(i);
            if (PackageXmlUtil.conditionEvaluatesToFalse(dep, format)) {
                continue;
            }
            if (dep.getPackage() == ROSPackage.ORPHAN
                    && depTrs.get(i).value() != depTrs.get(i)) {
                Annotation ann = holder.createErrorAnnotation(depTrs.get(i).value(),
                        "Unresolved dependency");
                ann.setHighlightType(ProblemHighlightType.ERROR);
                ann.registerFix(new RemoveDependencyQuickFix(pkgXml, i));
                if (pkgXml.getPackage().getProject().getService(ROSDepKeyCache.class).inOfflineMode()) {
                    ann.registerFix(new ForceCacheQuickFix());
                }
            }
        }
    }

    void annInvalidDependencyVersionAttr() {
        for (int i = 0; i < dependencies.size(); i++) {
            Dependency dep = dependencies.get(i);
            if (dep.getPackage() != ROSPackage.ORPHAN && dep.getVersionRange().isNotValid()) {
                for (TextRange tr : depTrs.get(i).attrQuery(null,
                        "version_lt","version_lte","version_gt","version_gte","version_eq")) {
                    Annotation ann = holder.createErrorAnnotation(tr, "Invalid version restriction(s).");
                    ann.registerFix(new AmputateDependencyQuickFix(pkgXml, i));
                    ann.registerFix(new FixDependencyQuickFix(pkgXml, i, false));
                }
            }
        }
    }

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
                        "version_lt","version_lte","version_gt","version_gte","version_eq");
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
                    Annotation ann = holder.createErrorAnnotation(tr,
                            "Conflicting version restrictions.");
                    ann.registerFix(new AmputateDependencyQuickFix(pkgXml, i));
                    ann.registerFix(new FixDependencyQuickFix(pkgXml, i, false));
                }
            }
        }
    }

    void annIgnoredCondition() {
        for (int i = 0; i < dependencies.size(); i++) {
            if (PackageXmlUtil.conditionEvaluatesToFalse(dependencies.get(i), format)) {
                Annotation ann = holder.createInfoAnnotation(depTrs.get(i), null);
                ann.setTextAttributes(ROSConditionSyntaxHighlighter.IGNORED);
            }
        }
    }
}
