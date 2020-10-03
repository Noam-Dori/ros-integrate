package ros.integrate.pkg.xml.annotate;

import com.intellij.codeInsight.daemon.impl.analysis.RemoveTagIntentionFix;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.TagTextRange;
import ros.integrate.pkg.xml.VersionRange;
import ros.integrate.pkg.xml.intention.*;

/**
 * A facade class used to annotate package.xml files for anything related to identifying tags (name, description,
 * version, format)
 */
class PackageIdAnnotator {
    @NotNull
    private final ROSPackageXml pkgXml;
    @NotNull
    private final AnnotationHolder holder;
    @NotNull
    private final TagTextRange nameTr, versionTr, descriptionTr;
    @Nullable
    private final String name, description;
    @Nullable
    private final ROSPackageXml.Version version;
    private final int format;

    /**
     * construct the annotator
     * @param pkgXml the reference package.xml file
     * @param holder the annotation holder.
     */
    @Contract(pure = true)
    PackageIdAnnotator(@NotNull ROSPackageXml pkgXml, @NotNull AnnotationHolder holder) {
        this.pkgXml = pkgXml;
        this.holder = holder;
        nameTr = pkgXml.getNameTextRange();
        versionTr = pkgXml.getVersionTextRange();
        descriptionTr = pkgXml.getDescriptionTextRange();
        name = pkgXml.getPkgName();
        version = pkgXml.getVersion();
        description = pkgXml.getDescription();
        format = pkgXml.getFormat();
    }


    /**
     * annotates packages that have no name tag
     */
    void annNoName() {
        if (name == null) {
            Annotation ann = holder.createErrorAnnotation(nameTr, "Package must give a name to the package");
            ann.registerFix(new FixNameQuickFix(pkgXml, "Add"));
        }
    }

    /**
     * annotates tags with names that are not lower case
     */
    void annNameNotLowercase() {
        if (name != null && !name.toLowerCase().equals(name)) {
            holder.createWarningAnnotation(nameTr.value(),
                    "While supported, package names should not have capital letters.");
        }
    }

    /**
     * annotates name tags that do not match the name of their parent directory
     */
    void annPkgNameMatch() {
        if (!pkgXml.getPackage().getName().equals(name)) {
            Annotation ann = holder.createErrorAnnotation(nameTr.value(),
                    "Package name should match its parent directory");
            ann.registerFix(new FixNameQuickFix(pkgXml, "Fix"));
        }
    }

    /**
     * annotates packages that have no version
     */
    void annNoVersion() {
        if (version == null) {
            Annotation ann = holder.createErrorAnnotation(versionTr,
                    "Package must have a version.");
            ann.registerFix(new FixVersionQuickFix(pkgXml, "Add"));
        }
    }

    /**
     * annotates version values that are malformed (for example 1.0.a is not an actual version)
     */
    void annBadVersion() {
        if (version != null && !version.getValue().matches(VersionRange.VERSION_REGEX)) {
            Annotation ann = holder.createErrorAnnotation(versionTr.value(),
                    "Invalid version: versions should be written in the form \"NUMBER.NUMBER.NUMBER\"");
            ann.registerFix(new FixVersionQuickFix(pkgXml, "Fix"));
        }
    }

    /**
     * annotates version compatibility values that are malformed (for example 1.0.a is not an actual version)
     */
    void annBadVersionCompatibility() {
        if (format >= 3 && version != null && version.getRawCompatibility() != null &&
                !version.getRawCompatibility().matches(VersionRange.VERSION_REGEX)) {
            Annotation ann = holder.createErrorAnnotation(versionTr.attrValue("compatibility"),
                    "Invalid version compatibility: versions should be written in the form \"NUMBER.NUMBER.NUMBER\"");
            ann.registerFix(new FixVersionQuickFix(pkgXml, "Fix"));
        }
    }

    /**
     * annotates packages that have no description tag
     */
    void annNoDescription() {
        if (description == null) {
            Annotation ann = holder.createErrorAnnotation(descriptionTr,
                    "Package must have a description.");
            ann.registerFix(new AddDescriptionQuickFix(pkgXml));
        }
    }

    /**
     * annotates name tags if more than one if found in the same package
     */
    void annTooManyNames() {
        annTooManyComponent("name");
    }

    /**
     * annotates version tags if more than one if found in the same package
     */
    void annTooManyVersions() {
        annTooManyComponent("version");
    }

    /**
     * annotates description tags if more than one if found in the same package
     */
    void annTooManyDescriptions() {
        annTooManyComponent("description");
    }

    private void annTooManyComponent(String compName) {
        XmlTag[] subTags = pkgXml.findSubTags(compName);
        if (subTags.length > 1) {
            for (int i = 1; i < subTags.length; i++) {
                Annotation ann = holder.createErrorAnnotation(subTags[i],"Too many " + compName + " tags.");
                ann.registerFix(new RemoveTagIntentionFix(compName, subTags[i]));
            }
        }
    }

    /**
     * annotates the version tag if its compatibility its higher than the actual version (for example version=1.0.0,
     * compatibility=2.0.0)
     */
    void annCompatibilityHigherThanVersion() {
        if (format < 3 || version == null || !version.getValue().matches(VersionRange.VERSION_REGEX)
                || !version.getCompatibility().matches(VersionRange.VERSION_REGEX)) {
            return;
        }
        if (new VersionRange.Builder().min(version.getCompatibility(), false)
                .max(version.getValue(), false).build().isNotValid()) {
            Annotation ann = holder.createErrorAnnotation(versionTr.name(),
                    "Invalid version tag: compatibility is higher than version.");
            ann.registerFix(new RemoveCompatibilityFix(pkgXml));
            ann.registerFix(new FlipVersionCompatibilityFix(pkgXml));
        }
    }
}
