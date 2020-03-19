package ros.integrate.pkg.xml.annotate;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.ROSDepKeyCache;
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.pkg.xml.DependencyType;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.ROSPackageXml.Dependency;
import ros.integrate.pkg.xml.intention.ForceCacheQuickFix;
import ros.integrate.pkg.xml.intention.ReformatPackageXmlFix;
import ros.integrate.pkg.xml.intention.RemoveDependencyQuickFix;

import java.util.*;
import java.util.stream.Collectors;

class PackageDependencyAnnotator {
    @NotNull
    private final ROSPackageXml pkgXml;
    @NotNull
    private final AnnotationHolder holder;

    @NotNull
    private final List<Dependency> dependencies;

    @NotNull
    private final List<Pair<TextRange, TextRange>> depTrs;

    @Contract(pure = true)
    PackageDependencyAnnotator(@NotNull ROSPackageXml pkgXml, @NotNull AnnotationHolder holder) {
        this.holder = holder;
        this.pkgXml = pkgXml;
        dependencies = pkgXml.getDependencies(null);
        depTrs = pkgXml.getDependencyTextRanges();
    }

    void annSelfDependency() {
        for (int i = 0; i < dependencies.size(); i++) {
            if (pkgXml.getPackage().equals(dependencies.get(i).getPackage())) {
                Annotation ann = holder.createErrorAnnotation(depTrs.get(i).second,
                        "A package cannot depend on itself.");
                ann.registerFix(new RemoveDependencyQuickFix(pkgXml, i));
            }
        }
    }

    void invalidDependencyName() {
        // get invalid tag names
        List<String> relevant = Arrays.stream(DependencyType.values())
                .filter(dep -> !dep.relevant(pkgXml.getFormat())).map(DependencyType::getTagName)
                .collect(Collectors.toList());
        for (int i = 0; i < dependencies.size(); i++) {
            String tagName = dependencies.get(i).getType().getTagName();
            if (relevant.contains(tagName)) {
                Annotation ann = holder.createErrorAnnotation(depTrs.get(i).first,
                        "Dependency tag " + tagName + " may not be used in manifest format " +
                                pkgXml.getFormat() + ".");
                ann.registerFix(new RemoveDependencyQuickFix(pkgXml, i));
                ann.registerFix(new ReformatPackageXmlFix(pkgXml, false));
            }
        }
    }

    void annEmptyDependency() {
        for (int i = 0; i < dependencies.size(); i++) {
            if (depTrs.get(i).second.getLength() == 0) {
                Annotation ann = holder.createErrorAnnotation(depTrs.get(i).first,
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
                if (dj.getPackage().equals(di.getPackage()) && !allCovered.isEmpty()) {
                    trsToAnn.add(j);
                    found = true;
                }
            }
            if (found) {
                trsToAnn.add(i);
            }
        }
        trsToAnn.forEach(i -> {
            Annotation ann = holder.createErrorAnnotation(depTrs.get(i).second,
                    "Dependency Tag conflicts with another tag in the file.");
            ann.registerFix(new RemoveDependencyQuickFix(pkgXml, i));
            ann.registerFix(new ReformatPackageXmlFix(pkgXml, false));
        });
    }

    void annDependencyNotFound() {
        for (int i = 0; i < dependencies.size(); i++) {
            if (dependencies.get(i).getPackage() == ROSPackage.ORPHAN) {
                Annotation ann = holder.createErrorAnnotation(depTrs.get(i).second,
                        "Unresolved dependency");
                ann.setHighlightType(ProblemHighlightType.ERROR);
                ann.registerFix(new RemoveDependencyQuickFix(pkgXml, i));
                if (pkgXml.getPackage().getProject().getService(ROSDepKeyCache.class).inOfflineMode()) {
                    ann.registerFix(new ForceCacheQuickFix());
                }
            }
        }
    }
}
