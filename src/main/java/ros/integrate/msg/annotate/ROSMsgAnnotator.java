package ros.integrate.msg.annotate;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ros.integrate.msg.psi.ROSPktConst;
import ros.integrate.msg.psi.ROSPktField;
import ros.integrate.msg.psi.ROSPktSeparator;

/**
 * This class is responsible for adding annotations to .msg (and .srv) files,
 * and directing results to specific annotators.
 */
public class ROSMsgAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        String msgName = element.getContainingFile().getName();

        if (element instanceof ROSPktField) {
            ROSPktField field = (ROSPktField) element;
            ROSMsgTypeAnnotator annotator = new ROSMsgTypeAnnotator(holder, field.getType(), msgName);


            if (field.getType().custom() != null) {
                annotator.annSelfContaining();
                if (!annotator.annTypeNotDefined()) {
                    annotator.annIllegalType();
                }
            }

            // constant inspection:
            ROSPktConst constant = field.getConst();
            if (constant != null) {
                boolean removeIntention = annotator.annArrayConst(false, constant), // only int,uint,float may use integer consts
                        badTypeActivated = annotator.annBadTypeConst(removeIntention, constant);
                annotator.annConstTooBig(badTypeActivated, constant);
            }

            String fieldName = field.getLabel().getText();
            if (fieldName != null) {
                ROSMsgLabelAnnotator nameAnnotator = new ROSMsgLabelAnnotator(holder, field.getLabel(), fieldName);
                nameAnnotator.annDuplicateLabel();
                nameAnnotator.annIllegalLabel();
            }

        } else if (element instanceof ROSPktSeparator) {
            ROSMsgSeparatorAnnotator annotator = new ROSMsgSeparatorAnnotator(holder,(ROSPktSeparator)element);
            annotator.annTooManySeparators();
        }
    }
}
