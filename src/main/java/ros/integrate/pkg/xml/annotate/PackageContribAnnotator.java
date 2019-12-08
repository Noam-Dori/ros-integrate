package ros.integrate.pkg.xml.annotate;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.ROSPackageXml.Contributor;
import ros.integrate.pkg.xml.intention.*;

import java.util.List;

/**
 * A facade class used to annotate package.xml files for anything related to the contributor type tags (maintainer,
 * author).
 */
class PackageContribAnnotator {
    @NotNull
    private final ROSPackageXml pkgXml;
    @NotNull
    private final List<Contributor> maintainers, authors;
    @NotNull
    private final List<TextRange> maintainerTr, authorTr;
    @NotNull
    private final AnnotationHolder holder;

    PackageContribAnnotator(@NotNull ROSPackageXml pkgXml, @NotNull AnnotationHolder holder) {
        this.pkgXml = pkgXml;
        this.holder = holder;
        maintainers = pkgXml.getMaintainers();
        authors = pkgXml.getAuthors();
        maintainerTr = pkgXml.getMaintainerTextRanges();
        authorTr = pkgXml.getAuthorTextRanges();
    }

    void annNoMaintainers() {
        if (maintainers.isEmpty()) {
            Annotation ann = holder.createErrorAnnotation(maintainerTr.get(0),
                    "package should have at least one maintainer.");
            ann.registerFix(new AddMaintainerQuickFix(pkgXml));
            ann.registerFix(new DeclareOrphanQuickFix(pkgXml));
        }
    }

    void annBadMaintainer() {
        for (int i = 0; i < maintainers.size(); i++) {
            Annotation ann = null;
            if (maintainers.get(i).getEmail().isEmpty()) {
                ann = holder.createErrorAnnotation(maintainerTr.get(i), "maintainer is missing reference email");
            }
            if (pkgXml.getMaintainers().get(i).getName().isEmpty()) {
                ann = holder.createErrorAnnotation(maintainerTr.get(i), "maintainer does not have a name");
            }
            if (ann != null) {
                ann.registerFix(new FixContributorQuickFix(pkgXml, i, ContribType.MAINTAINER));
                if (maintainers.size() > 1) {
                    ann.registerFix(new RemoveContributorFix(pkgXml, i, ContribType.MAINTAINER));
                }
            }
        }
    }

    void annBadAuthor() {
        for (int i = 0; i < authors.size(); i++) {
            if (authors.get(i).getName().isEmpty()) {
                Annotation ann = holder.createErrorAnnotation(authorTr.get(i), "author does not have a name");
                ann.registerFix(new FixContributorQuickFix(pkgXml, i, ContribType.AUTHOR));
            }
        }
    }
}
