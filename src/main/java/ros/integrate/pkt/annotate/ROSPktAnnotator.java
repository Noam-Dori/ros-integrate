package ros.integrate.pkt.annotate;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.psi.ROSPktConst;
import ros.integrate.pkt.psi.ROSPktField;
import ros.integrate.pkt.psi.ROSPktSeparator;

/**
 * This class is responsible for adding annotations to .pkt (and .srv) files,
 * and directing results to specific annotators.
 */
public class ROSPktAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        String msgName = element.getContainingFile().getName();

        if (element instanceof ROSPktField) {
            ROSPktField field = (ROSPktField) element;
            ROSPktTypeAnnotator annotator = new ROSPktTypeAnnotator(holder, field.getType(), msgName);


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
                ROSPktLabelAnnotator nameAnnotator = new ROSPktLabelAnnotator(holder, field.getLabel(), fieldName);
                nameAnnotator.annDuplicateLabel();
                nameAnnotator.annIllegalLabel();
            }

        } else if (element instanceof ROSPktSeparator) {
            ROSPktSeparatorAnnotator annotator = new ROSPktSeparatorAnnotator(holder,(ROSPktSeparator)element);
            annotator.annTooManySeparators();
        }
    }
}
