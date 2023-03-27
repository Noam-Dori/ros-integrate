package ros.integrate.pkt.annotate;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.intention.ChangeNameQuickFix;
import ros.integrate.pkt.psi.ROSPktFieldBase;
import ros.integrate.pkt.psi.ROSPktLabel;

/**
 * An annotator dedicated to {@link ROSPktLabel}, the name of each property in the message
 * @author Noam Dori
 */
public class ROSPktLabelAnnotator extends ROSPktAnnotatorBase {

    private final @NotNull
    String fieldName;
    private final @NotNull
    ROSPktLabel label;

    /**
     * construct the annotator
     *
     * @param holder    the annotation holder
     * @param label     the PSI element pointing to the name of the field
     * @param fieldName the name of the field
     */
    ROSPktLabelAnnotator(@NotNull AnnotationHolder holder,
                         @NotNull ROSPktLabel label,
                         @NotNull String fieldName) {
        super(holder);
        this.fieldName = fieldName;
        this.label = label;
    }

    /**
     * annotates if this label being used for two separate fields?
     */
    void annDuplicateLabel() {
        if (notFirstDefinition(label)) {
            TextRange range = new TextRange(label.getTextRange().getStartOffset(),
                    label.getTextRange().getEndOffset());
            holder.newAnnotation(HighlightSeverity.ERROR, "Field label '" + fieldName + "' is already used")
                    .range(range)
                    .withFix(new ChangeNameQuickFix((ROSPktFieldBase) label.getParent(), label))
                    .create();
        }
    }

    /**
     * annotates if this label does not follow naming rules (alphanumeric,starts with letter)
     */
    void annIllegalLabel() {
        String message = getIllegalLabelMessage(fieldName);
        if (message != null) {
            TextRange range = new TextRange(label.getTextRange().getStartOffset(),
                    label.getTextRange().getEndOffset());
            holder.newAnnotation(HighlightSeverity.ERROR, message)
                    .range(range)
                    .withFix(new ChangeNameQuickFix((ROSPktFieldBase) label.getParent(), label))
                    .create();
        }
    }

    @Nullable
    public static String getIllegalLabelMessage(@NotNull String labelText) {
        return labelText.matches("[a-zA-Z][a-zA-Z0-9_]*") ? null :
                labelText.matches("[a-zA-Z0-9_]+") ?
                        "Field names must start with a letter, not a number or underscore" :
                        "Field names may only contain alphanumeric characters or underscores";
    }

    /**
     * checks whether of not the label provided is the first label in this section that has its name.
     *
     * @param name the field to test
     * @return <code>true</code> if {@param field} is the first defined label with the provided name in this file,
     * <code>false</code> otherwise.
     */
    private boolean notFirstDefinition(@NotNull ROSPktLabel name) {
        for (ROSPktFieldBase field : name.getContainingSection().getFields(ROSPktFieldBase.class, false)) {
            if (field.getLabel() != null && name.getText().equals(field.getLabel().getText())) {
                return !name.equals(field.getLabel());
            }
        }
        return false;
    }
}
