package ros.integrate.pkg.xml.annotate;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.pkg.xml.DependencyType;
import ros.integrate.pkg.xml.NamedTextRange;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.intention.RemoveDependencyQuickFix;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class PackageDependencyAnnotator {
    @NotNull
    private final ROSPackageXml pkgXml;
    @NotNull
    private final AnnotationHolder holder;

    @NotNull
    private final List<ROSPackage> dependencies;

    @NotNull
    private final List<Pair<NamedTextRange,TextRange>> depTrs;

    @Contract(pure = true)
    PackageDependencyAnnotator(@NotNull ROSPackageXml pkgXml, @NotNull AnnotationHolder holder) {
        this.holder = holder;
        this.pkgXml = pkgXml;
        dependencies = pkgXml.getDependencies(null);
        depTrs = pkgXml.getDependencyTextRanges();
    }

    void annSelfDependency() {
        for (int i = 0; i < dependencies.size(); i++) {
            if (dependencies.get(i).equals(pkgXml.getPackage())) {
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
        for (int i = 0; i < depTrs.size(); i++) {
            String tagName = depTrs.get(i).first.getContent();
            if (relevant.contains(tagName)) {
                Annotation ann = holder.createErrorAnnotation(depTrs.get(i).first,
                        "Dependency tag " + tagName + " may not be used in manifest format " +
                                pkgXml.getFormat() + ".");
                ann.registerFix(new RemoveDependencyQuickFix(pkgXml, i));
//                ann.registerFix(new UpdateDependencyQuickFix(pkgXml, i));
            }
        }
    }
}
