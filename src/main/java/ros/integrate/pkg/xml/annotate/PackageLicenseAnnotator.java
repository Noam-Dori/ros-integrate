package ros.integrate.pkg.xml.annotate;

import com.intellij.lang.annotation.AnnotationBuilder;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.ROSPackageXml.License;
import ros.integrate.pkg.xml.TagTextRange;
import ros.integrate.pkg.xml.intention.AddLicenceQuickFix;
import ros.integrate.pkg.xml.intention.FixLicenseQuickFix;
import ros.integrate.pkg.xml.intention.RemoveLicenseQuickFix;
import ros.integrate.pkg.xml.intention.SplitLicenseQuickFix;

import java.util.List;

/**
 * A facade class used to annotate package.xml files for anything related to the license tag
 */
class PackageLicenseAnnotator {
    @NotNull
    private final ROSPackageXml pkgXml;
    @NotNull
    private final AnnotationHolder holder;
    @NotNull
    private final List<License> licenses;
    @NotNull
    private final List<TagTextRange> licenseTrs;

    /**
     * construct the annotator
     *
     * @param pkgXml the reference package.xml file
     * @param holder the annotation holder.
     */
    PackageLicenseAnnotator(@NotNull ROSPackageXml pkgXml, @NotNull AnnotationHolder holder) {
        this.pkgXml = pkgXml;
        this.holder = holder;
        licenses = pkgXml.getLicences();
        licenseTrs = pkgXml.getLicenceTextRanges();
    }

    /**
     * annotates packages with no license tags
     */
    void annNoLicenses() {
        if (licenses.isEmpty()) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Package must have at least one licence.")
                    .range(licenseTrs.get(0))
                    .withFix(new AddLicenceQuickFix(pkgXml))
                    .create();
        }
    }

    /**
     * annotates license tags if either:
     * <ul>
     *     <li>the tag has no value</li>
     *     <li>the tag contains more than one license (found by comma)</li>
     * </ul>
     */
    void annBadLicenses() {
        if (licenses.isEmpty()) {
            return;
        }
        for (int i = 0; i < licenses.size(); i++) {
            License license = licenses.get(i);
            TagTextRange tr = licenseTrs.get(i);
            if (license.getValue().isEmpty()) {
                AnnotationBuilder ann = holder.newAnnotation(HighlightSeverity.ERROR, "License tags cannot be empty.")
                        .range(tr)
                        .withFix(new FixLicenseQuickFix(pkgXml, i));
                if (licenses.size() > 1) {
                    ann = ann.withFix(new RemoveLicenseQuickFix(pkgXml, i));
                }
                ann.create();
            } else if (license.getValue().contains(",")) {
                holder.newAnnotation(HighlightSeverity.WARNING, "Each license tag must only hold ONE license.")
                        .range(tr.value())
                        .withFix(new SplitLicenseQuickFix(pkgXml, i))
                        .create();
            }
        }
    }

    /**
     * annotates placeholder license tags
     */
    void annTodoLicense() {
        if (licenses.isEmpty()) {
            return;
        }
        for (int i = 0; i < licenses.size(); i++) {
            License license = licenses.get(i);
            TagTextRange tr = licenseTrs.get(i);
            if (license.getValue().matches("TODO")) {
                AnnotationBuilder ann = holder.newAnnotation(HighlightSeverity.WEAK_WARNING,
                        "This only acts a placeholder for an actual license, please choose one")
                        .range(tr.value());
                if (licenses.size() > 1) {
                    ann = ann.withFix(new RemoveLicenseQuickFix(pkgXml, i));
                }
                ann.create();
            }
        }
    }
}
