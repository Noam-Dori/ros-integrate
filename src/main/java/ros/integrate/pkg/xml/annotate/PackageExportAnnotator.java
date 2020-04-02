package ros.integrate.pkg.xml.annotate;

import com.intellij.codeInsight.daemon.impl.analysis.RemoveTagIntentionFix;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.xml.ExportTag;
import ros.integrate.pkg.xml.ROSPackageXml;

import java.util.Arrays;

class PackageExportAnnotator {
    @Nullable
    private final ExportTag export;

    @NotNull
    private final AnnotationHolder holder;

    public PackageExportAnnotator(@NotNull ROSPackageXml pkgXml, @NotNull AnnotationHolder holder) {
        this.export = pkgXml.getExport();
        this.holder = holder;
    }

    private void tooManyTag(String tagName, String message) {
        if (export == null) {
            return;
        }
        XmlTag[] foundTags = export.getRawTag().findSubTags(tagName);
        for (int i = 1; i < foundTags.length; i++) {
            holder.createWarningAnnotation(foundTags[i].getTextRange(), message)
                    .registerFix(new RemoveTagIntentionFix(tagName, foundTags[i]));
        }
    }

    private void emptinessCheckFailed(String tagName, boolean emptyFails) {
        if (export == null) {
            return;
        }
        Arrays.stream(export.getRawTag().findSubTags(tagName))
                .filter(tag -> emptyFails ? tag.getValue().getText().isEmpty() : !tag.isEmpty())
                .forEach(tag -> holder.createWarningAnnotation(
                        emptyFails ? tag.getTextRange() : tag.getValue().getTextRange(),
                        "Tag " + tagName + " should " + (emptyFails ? "not " : "") + "be empty.")
                        .registerFix(new RemoveTagIntentionFix(tagName, tag)));
    }

    void annEmptyMessageGenerator() {
        emptinessCheckFailed("message_generator", true);
    }

    void annMultipleMessageGenerators() {
        tooManyTag("message_generator", "Each package may only generate code for one language at most.");
    }

    void annNonEmptyArchitectureIndependentTags() {
        emptinessCheckFailed("architecture_independent", false);
    }

    void annMultipleArchitectureIndependentTags() {
        tooManyTag("architecture_independent", "Multiple architecture independent annotations found.");
    }

    void annMultipleDeprecated() {
        tooManyTag("deprecated", "Multiple deprecation messages found.");
    }

    void annNonEmptyMetapackageTag() {
        emptinessCheckFailed("metapackage", false);
    }

    void annMultipleMetapackageTags() {
        tooManyTag("metapackage", "Multiple metapackage annotations found.");
    }

    void annEmptyBuildType() {
        emptinessCheckFailed("build_type", true);
    }

    void annMultipleBuildTypes() {
        tooManyTag("build_type", "A package may only have one build type.");
    }
}
