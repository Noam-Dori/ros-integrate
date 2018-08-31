package ros.integrate.msg.annotate;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ros.integrate.msg.ROSMsgUtil;
import ros.integrate.msg.psi.ROSMsgProperty;
import ros.integrate.msg.psi.ROSMsgSeparator;

public class ROSMsgAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        Project project = element.getProject();
        String msgName = ROSMsgUtil.trimMsgFileName(element.getContainingFile().getName());

        if (element instanceof ROSMsgProperty) {
            ROSMsgProperty prop = (ROSMsgProperty) element;
            ROSMsgTypeAnnotator annotator = new ROSMsgTypeAnnotator(holder, project, prop, msgName);

            String fieldType = prop.getCustomType();
            if (fieldType != null) {
                annotator.setFieldType(fieldType);
                annotator.annSelfContaining();
                if(!annotator.annTypeNotDefined()) {
                    annotator.annIllegalType();
                }
            }

            fieldType = prop.getRawType();
            if(fieldType != null) {
                annotator.setFieldType(fieldType);

                // constant inspection:
                String constant = null;
                if (prop.getConst() != null) {
                    constant = prop.getConst().getText();
                }
                if(constant != null) {
                    boolean removeIntention = annotator.annArrayConst(false, constant), // only int,uint,float may use integer consts
                            badTypeActivated = annotator.annBadTypeConst(removeIntention,constant);
                    annotator.annConstTooBig(badTypeActivated,constant);
                }
            }

            String fieldName = prop.getFieldName().getText();
            if(fieldName != null) {
                ROSMsgNameAnnotator nameAnnotator = new ROSMsgNameAnnotator(holder, prop, fieldName);
                nameAnnotator.annDuplicateName();
                nameAnnotator.annIllegalName();
            }

        } else if (element instanceof ROSMsgSeparator) {
            ROSMsgSeparatorAnnotator annotator = new ROSMsgSeparatorAnnotator(holder,(ROSMsgSeparator)element);
            annotator.annTooManySeparators(0,"ROS Messages cannot have service separators");
        }
    }
}
