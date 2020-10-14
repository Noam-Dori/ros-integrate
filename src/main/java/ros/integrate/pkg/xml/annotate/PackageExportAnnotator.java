package ros.integrate.pkg.xml.annotate;

import com.intellij.codeInsight.daemon.impl.analysis.RemoveTagIntentionFix;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.xml.ExportTag;
import ros.integrate.pkg.xml.PackageXmlUtil;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.TagTextRange;
import ros.integrate.pkg.xml.condition.highlight.ROSConditionSyntaxHighlighter;
import ros.integrate.pkg.xml.intention.RemoveBuildTypeQuickFix;

import java.util.Arrays;
import java.util.List;

/**
 * A facade class used to annotate package.xml files for anything related to the export tag
 */
class PackageExportAnnotator {
    @Nullable
    private final ExportTag export;

    @NotNull
    private final AnnotationHolder holder;

    /**
     * construct the annotator
     *
     * @param pkgXml the reference package.xml file
     * @param holder the annotation holder.
     */
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
            holder.newAnnotation(HighlightSeverity.WARNING, message)
                    .range(foundTags[i].getTextRange())
                    .withFix(new RemoveTagIntentionFix(tagName, foundTags[i]))
                    .create();
        }
    }

    private void emptinessCheckFailed(String tagName, boolean emptyFails) {
        if (export == null) {
            return;
        }
        Arrays.stream(export.getRawTag().findSubTags(tagName))
                .filter(tag -> emptyFails ? tag.getValue().getText().isEmpty() : !tag.isEmpty())
                .forEach(tag -> holder.newAnnotation(HighlightSeverity.WARNING,
                        "Tag " + tagName + " should " + (emptyFails ? "not " : "") + "be empty.")
                        .range(emptyFails ? tag.getTextRange() : tag.getValue().getTextRange())
                        .withFix(new RemoveTagIntentionFix(tagName, tag))
                        .create());
    }

    /**
     * annotates message_generator tags if they are empty
     */
    void annEmptyMessageGenerator() {
        emptinessCheckFailed("message_generator", true);
    }

    /**
     * annotates message_generator tags if two are found with the same value
     */
    void annMultipleMessageGenerators() {
        tooManyTag("message_generator", "Each package may only generate code for one language at most.");
    }

    /**
     * annotates architecture_independent tags if they contain a value
     */
    void annNonEmptyArchitectureIndependentTags() {
        emptinessCheckFailed("architecture_independent", false);
    }

    /**
     * annotates architecture_independent tags if more than one is found for the same package
     */
    void annMultipleArchitectureIndependentTags() {
        tooManyTag("architecture_independent", "Multiple architecture independent annotations found.");
    }

    /**
     * annotates deprecated tags if more than one is found for the same package
     */
    void annMultipleDeprecated() {
        tooManyTag("deprecated", "Multiple deprecation messages found.");
    }

    /**
     * annotates metapackage tags if they contain a value
     */
    void annNonEmptyMetapackageTag() {
        emptinessCheckFailed("metapackage", false);
    }

    /**
     * annotates metapackage tags if more than one if found for the same package
     */
    void annMultipleMetapackageTags() {
        tooManyTag("metapackage", "Multiple metapackage annotations found.");
    }

    /**
     * annotates build_type tags if they do not contain a value
     */
    void annEmptyBuildType() {
        emptinessCheckFailed("build_type", true);
    }

    /**
     * annotates build_type tags if more than one is found for the same package. This annotation respects conditions.
     */
    void annMultipleBuildTypes() {
        if (export == null) {
            return;
        }
        int format = export.getParent().getFormat();
        List<ExportTag.BuildType> buildTypes = export.getBuildTypes();
        List<TagTextRange> buildTypeTrs = export.getBuildTypeTextRanges();
        boolean foundActive = false;
        for (int i = 0; i < buildTypes.size(); i++) {
            if (!PackageXmlUtil.conditionEvaluatesToFalse(buildTypes.get(i).getCondition(), format)) {
                if (foundActive) {
                    holder.newAnnotation(HighlightSeverity.WARNING, "A package may only have one build type.")
                            .range(buildTypeTrs.get(i))
                            .withFix(new RemoveBuildTypeQuickFix(export, i))
                            .create();
                } else {
                    foundActive = true;
                }
            }
        }
    }

    /**
     * annotates tags that contains a false evaluating condition.
     */
    void annIgnoredCondition() {
        if (export == null) {
            return;
        }
        int format = export.getParent().getFormat();
        List<ExportTag.BuildType> buildTypes = export.getBuildTypes();
        List<TagTextRange> buildTypeTrs = export.getBuildTypeTextRanges();
        for (int i = 0; i < buildTypes.size(); i++) {
            if (PackageXmlUtil.conditionEvaluatesToFalse(buildTypes.get(i).getCondition(), format)) {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(buildTypeTrs.get(i))
                        .textAttributes(ROSConditionSyntaxHighlighter.IGNORED)
                        .create();
            }
        }
    }
}
