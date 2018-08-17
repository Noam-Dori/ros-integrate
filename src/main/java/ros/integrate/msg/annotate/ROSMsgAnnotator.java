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

            String fieldType = prop.getType();
            if (fieldType != null) {
                annotator.setFieldType(fieldType);
                annotator.annSelfContaining();
                annotator.annTypeNotDefined();
            }

            fieldType = prop.getGeneralType();
            if(fieldType != null) {
                annotator.setFieldType(fieldType);

                // constant inspection:
                String constant = prop.getCConst();
                if(constant != null) {
                    boolean removeIntention = annotator.annArrayConst(false, constant), // only int,uint,float may use integer consts
                            badTypeActivated = annotator.annBadTypeConst(removeIntention,constant);
                    annotator.annConstTooBig(badTypeActivated,constant);
                }
            }

            //TODO: duplicate name inspection
            //TODO: fix duplicate by changing name

            //TODO: actual inspection detecting proper naming for ROS msg (two inspections - one here for making sure the name is legal, the other is a warning which checks for CamelCase)

        } else if (element instanceof ROSMsgSeparator) {
            ROSMsgSeparatorAnnotator annotator = new ROSMsgSeparatorAnnotator(holder,(ROSMsgSeparator)element);
            annotator.annTooManySeparators(0);
        }
    }
}
