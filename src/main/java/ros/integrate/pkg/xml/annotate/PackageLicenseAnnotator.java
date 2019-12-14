package ros.integrate.pkg.xml.annotate;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.intention.AddLicenceQuickFix;
import ros.integrate.pkg.xml.intention.FixLicenseQuickFix;
import ros.integrate.pkg.xml.intention.RemoveLicenseQuickFix;
import ros.integrate.pkg.xml.intention.SplitLicenseQuickFix;

import java.util.List;

class PackageLicenseAnnotator {
    @NotNull
    private final ROSPackageXml pkgXml;
    @NotNull
    private final AnnotationHolder holder;
    @NotNull
    private final List<String> licenses;
    @NotNull
    private final List<TextRange> licenseTrs;

    PackageLicenseAnnotator(@NotNull ROSPackageXml pkgXml, @NotNull AnnotationHolder holder) {
        this.pkgXml = pkgXml;
        this.holder = holder;
        licenses = pkgXml.getLicences();
        licenseTrs = pkgXml.getLicenceTextRanges();
    }

    void annNoLicenses() {
        if (licenses.isEmpty()) {
            Annotation ann = holder.createErrorAnnotation(licenseTrs.get(0),
                    "package must have at least one licence.");
            ann.registerFix(new AddLicenceQuickFix(pkgXml));
        }
    }

    void annBadLicenses() {
        if (licenses.isEmpty()) {
            return;
        }
        for (int i = 0; i < licenses.size(); i++) {
            String license = licenses.get(i);
            TextRange tr = licenseTrs.get(i);
            if (license.isEmpty()) {
                Annotation ann = holder.createErrorAnnotation(tr,
                        "license tags cannot be empty.");
                ann.registerFix(new FixLicenseQuickFix(pkgXml, i));
                if (licenses.size() > 1) {
                    ann.registerFix(new RemoveLicenseQuickFix(pkgXml, i));
                }
            } else if (license.contains(",")) {
                Annotation ann = holder.createWarningAnnotation(tr,
                        "each license tag must only hold ONE license.");
                ann.registerFix(new SplitLicenseQuickFix(pkgXml, i));
            }
        }
    }

    void annTodoLicense() {
        if (licenses.isEmpty()) {
            return;
        }
        for (int i = 0; i < licenses.size(); i++) {
            String license = licenses.get(i);
            TextRange tr = licenseTrs.get(i);
            if (license.matches("TODO")) {
                Annotation ann = holder.createWeakWarningAnnotation(tr,
                        "This only acts a placeholder for an actual license, please choose one");
                if (licenses.size() > 1) {
                    ann.registerFix(new RemoveLicenseQuickFix(pkgXml, i));
                }
            }
        }
    }
}
