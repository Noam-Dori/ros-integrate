package ros.integrate.msg.annotate;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import ros.integrate.msg.intention.ChangeNameQuickFix;
import ros.integrate.msg.psi.ROSPktFile;
import ros.integrate.msg.psi.ROSPktField;
import ros.integrate.msg.psi.ROSPktLabel;

/**
 * An annotator dedicated to {@link ROSPktLabel}
 */
class ROSMsgLabelAnnotator extends ROSMsgAnnotatorBase {

    private final @NotNull String fieldName;
    private final @NotNull
    ROSPktLabel label;

    ROSMsgLabelAnnotator(@NotNull AnnotationHolder holder,
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
        ROSPktFile file = (ROSPktFile) label.getContainingFile();
        int nameCount = file.countNameInFile(fieldName);
        if (nameCount > 1 && !file.isFirstDefinition(label)) {
            TextRange range = new TextRange(label.getTextRange().getStartOffset(),
                    label.getTextRange().getEndOffset());
            Annotation ann = holder.createErrorAnnotation(range, "Field label '" + fieldName + "' is already used");
            ann.registerFix(new ChangeNameQuickFix((ROSPktField) label.getParent(), label));
        }
    }

    /**
     * annotates if this label does not follow naming rules (alphanumeric,starts with letter)
     */
    void annIllegalLabel() {
        String regex = "[a-zA-Z][a-zA-Z0-9_]*";
        if(!fieldName.matches(regex)) {
            TextRange range = new TextRange(label.getTextRange().getStartOffset(),
                    label.getTextRange().getEndOffset());
            String message;
            if(fieldName.matches("[a-zA-Z0-9_]+")) {
                message = "Field names must start with a letter, not a number or underscore";
            } else {
                message = "Field names may only contain alphanumeric characters or underscores";
            }
            Annotation ann = holder.createErrorAnnotation(range, message);
            ann.registerFix(new ChangeNameQuickFix((ROSPktField) label.getParent(), label));
        }
    }

    //TODO: a warning which checks for snake_case in names. make sure to check JB-inspection first
    //TODO: fix: offer conversion to snake_case
}
