package com.perfetto.ros.integrate;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.perfetto.ros.integrate.intention.RemoveAllSrvLinesQuickFix;
import com.perfetto.ros.integrate.psi.ROSMsgProperty;
import com.perfetto.ros.integrate.psi.ROSMsgSeparator;
import com.perfetto.ros.integrate.intention.RemoveSrvLineQuickFix;
import com.perfetto.ros.integrate.psi.ROSMsgTypes;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class ROSMsgAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof ROSMsgProperty) {
            ROSMsgProperty prop = (ROSMsgProperty) element;
            String value = prop.getType();

            if (value != null) {
                Project project = element.getProject();

                // type search
                // TODO: Search outside project (include files) for 'slashed' msgs
                // TODO: if catkin is defined, use it to search for msgs.
                List<String> types = ROSMsgUtil.findProjectMsgNames(project, value,
                        element.getContainingFile().getVirtualFile());
                if (types.size() == 0 && // if we need special annotation for headers, then sure.
                        !(value.equals("Header") && element.getTextRange().getStartOffset() == 0) &&
                        !value.contains("/")) {
                    TextRange range = new TextRange(element.getTextRange().getStartOffset(),
                            element.getTextRange().getStartOffset() + value.length());
                    holder.createInfoAnnotation(range, "Unresolved message object")
                            .setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
                }

                // constant inspection:
                // only int,uint,float may use integer consts
                // strings are the only type which can use str consts.
                // In case of array found:
                // TODO: Fixes: 1. remove array, 2. remove const
                // In case of time,duration,<other> found:
                // TODO: Fixes: 1. change to string 2. remove const
                if(prop.getConst() != null) {
                    if(prop.getArraySize() != -1) {
                        int start = Objects.requireNonNull(element.getNode().findChildByType(ROSMsgTypes.LBRACKET)).getStartOffset();
                        int end = Objects.requireNonNull(element.getNode().findChildByType(ROSMsgTypes.RBRACKET)).getStartOffset() + 1;
                        TextRange range = new TextRange(start, end);
                        Annotation ann = holder.createErrorAnnotation(range, "Array fields cannot be assigned a constant.");
                        ann.registerFix(null);
                        ann.registerFix(null);
                    }
                }
            }
        } else if (element instanceof ROSMsgSeparator) {
            // Too many service separators annotation
            if (ROSMsgUtil.countServiceSeparators(element.getContainingFile()) > 0) { // in Srv files this is 1
                TextRange range = new TextRange(element.getTextRange().getStartOffset(),
                        element.getTextRange().getEndOffset());
                Annotation ann = holder.createErrorAnnotation(range, "ROS Messages cannot have service separators");
                ann.registerFix(new RemoveSrvLineQuickFix((ROSMsgSeparator)element));
                ann.registerFix(new RemoveAllSrvLinesQuickFix());
            }
        }
    }
}
