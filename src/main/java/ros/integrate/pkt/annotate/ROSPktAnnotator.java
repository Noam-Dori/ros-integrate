package ros.integrate.pkt.annotate;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.psi.*;

/**
 * enforces rules for packet files (.msg, .srv, .action files) using annotations and intentions.
 * @author Noam Dori
 */
public class ROSPktAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof ROSPktFieldBase) {
            ROSPktFieldBase field = (ROSPktFieldBase) element;
            String msgName = field.getContainingFile().getPacketName();
            ROSPktFieldAnnotator fieldAnnotator = new ROSPktFieldAnnotator(holder, field);
            ROSPktTypeAnnotator typeAnnotator = new ROSPktTypeAnnotator(holder, field.getTypeBase(), msgName);
            fieldAnnotator.annBadStructure();
            typeAnnotator.annBadStructure();

            if (field.getTypeBase().custom() != null) {
                typeAnnotator.annSelfContaining();
                if (!typeAnnotator.annTypeNotDefined()) {
                    typeAnnotator.annIllegalType();
                }
            }

            // constant inspection:
            ROSPktConst constant = field.getConst();
            if (constant != null) {
                boolean removeIntention = typeAnnotator.annArrayConst(false, constant), // only int,uint,float may use integer consts
                        badTypeActivated = typeAnnotator.annBadTypeConst(removeIntention, constant);
                typeAnnotator.annConstTooBig(badTypeActivated, constant);
            }

            ROSPktLabel label = field.getLabel();
            if (label != null && label.getName() != null) {
                ROSPktLabelAnnotator nameAnnotator = new ROSPktLabelAnnotator(holder, label, label.getName());
                nameAnnotator.annDuplicateLabel();
                nameAnnotator.annIllegalLabel();
            }

        } else if (element instanceof ROSPktSeparator) {
            ROSPktSeparatorAnnotator annotator = new ROSPktSeparatorAnnotator(holder,(ROSPktSeparator)element);
            annotator.annTooManySeparators();
        }
    }
}
