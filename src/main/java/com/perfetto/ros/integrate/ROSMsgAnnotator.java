package com.perfetto.ros.integrate;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.perfetto.ros.integrate.intention.*;
import com.perfetto.ros.integrate.psi.ROSMsgProperty;
import com.perfetto.ros.integrate.psi.ROSMsgSeparator;
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
            }
            value = prop.getGeneralType();
            if(value != null) {

                // constant inspection:
                // only int,uint,float may use integer consts
                // strings are the only type which can use str consts.
                if(prop.getCConst() != null) {
                    boolean hasArrayAnn = false;
                    if(prop.getArraySize() != -1) {
                        int start = Objects.requireNonNull(element.getNode().findChildByType(ROSMsgTypes.CONST)).getStartOffset();
                        TextRange range = new TextRange(start, start + prop.getCConst().length());
                        Annotation ann = holder.createErrorAnnotation(range, "Array fields cannot be assigned a constant.");
                        ann.registerFix(new RemoveConstQuickFix(prop)); // remove const
                        ann.registerFix(new RemoveArrayQuickFix(prop)); // remove arr
                        hasArrayAnn = true;
                    }

                    if(!value.matches("(bool)|(string)|((uint|int)(8|16|32|64))|(float(32|64))")) { // a very simple regex
                        int start = Objects.requireNonNull(element.getNode().findChildByType(ROSMsgTypes.CONST)).getStartOffset();
                        TextRange range = new TextRange(start, start + prop.getCConst().length());
                        Annotation ann = holder.createErrorAnnotation(range, "Only the built-in string and numerical types may be assigned a constant.");
                        if(!hasArrayAnn) {
                            ann.registerFix(new RemoveConstQuickFix(prop)); // remove const
                        }
                        ann.registerFix(new ChangeKeytypeQuickFix(prop)); // change type
                    } else {
                        // builtin inspection:
                        // booleans may only have 1 or 0 as const
                        // unsigned integrals cannot have negative sign
                        // TODO: fix for bool is convert const to 'uint8/int8'
                        // TODO: fix for uint<> is convert to int<>
                    }
                }
            }
        } else if (element instanceof ROSMsgSeparator) {
            // Too many service separators annotation
            int separatorCount = ROSMsgUtil.countServiceSeparators(element.getContainingFile());
            if (separatorCount > 0) { // in Srv files this is 1
                TextRange range = new TextRange(element.getTextRange().getStartOffset(),
                        element.getTextRange().getEndOffset());
                Annotation ann = holder.createErrorAnnotation(range, "ROS Messages cannot have service separators");
                ann.registerFix(new RemoveSrvLineQuickFix((ROSMsgSeparator)element));
                if(separatorCount > 1) {ann.registerFix(new RemoveAllSrvLinesQuickFix());}
            }
        }
    }
}
