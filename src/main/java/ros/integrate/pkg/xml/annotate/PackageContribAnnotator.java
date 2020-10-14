package ros.integrate.pkg.xml.annotate;

import com.intellij.lang.annotation.AnnotationBuilder;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import ros.integrate.pkg.xml.TagTextRange;
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
    private final List<TagTextRange> maintainerTr, authorTr;
    @NotNull
    private final AnnotationHolder holder;

    /**
     * construct the annotator
     *
     * @param pkgXml the reference package.xml file
     * @param holder the annotation holder.
     */
    PackageContribAnnotator(@NotNull ROSPackageXml pkgXml, @NotNull AnnotationHolder holder) {
        this.pkgXml = pkgXml;
        this.holder = holder;
        maintainers = pkgXml.getMaintainers();
        authors = pkgXml.getAuthors();
        maintainerTr = pkgXml.getMaintainerTextRanges();
        authorTr = pkgXml.getAuthorTextRanges();
    }

    /**
     * annotates package that have no maintainers as errors.
     */
    void annNoMaintainers() {
        if (maintainers.isEmpty()) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Package should have at least one maintainer.")
                    .range(maintainerTr.get(0))
                    .withFix(new AddMaintainerQuickFix(pkgXml))
                    .withFix(new DeclareOrphanQuickFix(pkgXml))
                    .create();
        }
    }

    /**
     * annotates maintainer tags that are either:
     * <ul>
     *     <li>missing email</li>
     *     <li>have an email value that's not a real email</li>
     *     <li>missing name</li>
     * </ul>
     */
    void annBadMaintainer() {
        for (int i = 0; i < maintainers.size(); i++) {
            Contributor maintainer = maintainers.get(i);
            AnnotationBuilder ann = null;
            if (maintainer.getEmail().isEmpty()) {
                ann = holder.newAnnotation(HighlightSeverity.ERROR, "Maintainer is missing reference email")
                        .range(maintainerTr.get(i).name());
            } else if (!maintainer.getEmail().matches(Contributor.EMAIL_REGEX)) {
                ann = holder.newAnnotation(HighlightSeverity.ERROR, "Maintainer email is invalid")
                        .range(maintainerTr.get(i).attrValue("email"));
            }
            if (maintainer.getName().isEmpty()) {
                ann = holder.newAnnotation(HighlightSeverity.ERROR, "Maintainer does not have a name")
                        .range(maintainerTr.get(i).name());
            }
            if (ann != null) {
                ann = ann.withFix(new FixContributorQuickFix(pkgXml, i, ContribType.MAINTAINER));
                if (maintainers.size() > 1) {
                    ann = ann.withFix(new RemoveContributorFix(pkgXml, i, ContribType.MAINTAINER));
                }
                ann.create();
            }
        }
    }

    /**
     * annotates maintainer tags that are either:
     * <ul>
     *     <li>have an email value that's not a real email</li>
     *     <li>missing name</li>
     * </ul>
     */
    void annBadAuthor() {
        for (int i = 0; i < authors.size(); i++) {
            Contributor author = authors.get(i);
            if (author.getName().isEmpty()) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Author does not have a name")
                        .range(authorTr.get(i).name())
                        .withFix(new FixContributorQuickFix(pkgXml, i, ContribType.AUTHOR))
                        .withFix(new RemoveContributorFix(pkgXml, i, ContribType.AUTHOR))
                        .create();
            } else if (!author.getEmail().isEmpty() && !author.getEmail().matches(Contributor.EMAIL_REGEX)) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Author email is invalid")
                        .range(authorTr.get(i).attrValue("email"))
                        .withFix(new FixContributorQuickFix(pkgXml, i, ContribType.AUTHOR))
                        .withFix(new RemoveContributorFix(pkgXml, i, ContribType.AUTHOR))
                        .create();
            }
        }
    }
}
