package com.perfetto.ros.integrate;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.perfetto.ros.integrate.psi.ROSMsgProperty;
import com.perfetto.ros.integrate.psi.ROSMsgSeparator;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ROSMsgAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof ROSMsgProperty) {
            ROSMsgProperty rosMsgProperty = (ROSMsgProperty) element;
            String value = rosMsgProperty.getType();

            if (value != null) {
                Project project = element.getProject();

                List<String> types = ROSMsgUtil.findProjectMsgNames(project, value,
                        element.getContainingFile().getVirtualFile());
                if (types.size() == 0 && // if we need special annotation for headers, then sure.
                        !(value.equals("Header") && element.getTextRange().getStartOffset() == 0) &&
                        !value.contains("/")) {
                    TextRange range = new TextRange(element.getTextRange().getStartOffset(),
                            element.getTextRange().getStartOffset() + value.length());
                    Annotation ann = holder.createInfoAnnotation(range, "Unresolved message object");
                    ann.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
                }
            }
        } else if (element instanceof ROSMsgSeparator) {
            if (ROSMsgUtil.countServiceSeparators(element.getContainingFile()) > 0) { // in Srv files this is 1
                TextRange range = new TextRange(element.getTextRange().getStartOffset(),
                        element.getTextRange().getEndOffset());
                holder.createErrorAnnotation(range, "ROS Messages cannot have service separators")
                        .registerFix(new RemoveSrvLineQuickFix((ROSMsgSeparator)element));
            }
        }
    }
}
