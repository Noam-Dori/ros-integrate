package ros.integrate.msg.annotate;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ros.integrate.msg.psi.ROSMsgConst;
import ros.integrate.msg.psi.ROSMsgField;
import ros.integrate.msg.psi.ROSMsgSeparator;

public class ROSMsgAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        Project project = element.getProject();
        String msgName = element.getContainingFile().getName();

        if (element instanceof ROSMsgField) {
            ROSMsgField field = (ROSMsgField) element;
            ROSMsgTypeAnnotator annotator = new ROSMsgTypeAnnotator(holder, project, field.getType(), msgName);


            if (field.getType().custom() != null) {
                annotator.annSelfContaining();
                if (!annotator.annTypeNotDefined()) {
                    annotator.annIllegalType();
                }
            }

            // constant inspection:
            ROSMsgConst constant = field.getConst();
            if (constant != null) {
                boolean removeIntention = annotator.annArrayConst(false, constant), // only int,uint,float may use integer consts
                        badTypeActivated = annotator.annBadTypeConst(removeIntention, constant);
                annotator.annConstTooBig(badTypeActivated, constant);
            }

            String fieldName = field.getLabel().getText();
            if (fieldName != null) {
                ROSMsgNameAnnotator nameAnnotator = new ROSMsgNameAnnotator(holder, field.getLabel(), fieldName);
                nameAnnotator.annDuplicateName();
                nameAnnotator.annIllegalName();
            }

        } else if (element instanceof ROSMsgSeparator) {
            ROSMsgSeparatorAnnotator annotator = new ROSMsgSeparatorAnnotator(holder,(ROSMsgSeparator)element);
            annotator.annTooManySeparators(0,"ROS Messages cannot have service separators");
        }
    }
}
