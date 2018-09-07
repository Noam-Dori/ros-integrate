package ros.integrate.msg.annotate;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ros.integrate.msg.ROSMsgUtil;
import ros.integrate.msg.intention.ChangeNameQuickFix;
import ros.integrate.msg.psi.ROSMsgLabel;
import ros.integrate.msg.psi.ROSMsgField;

class ROSMsgNameAnnotator {

    private final @NotNull AnnotationHolder holder;
    private final @NotNull String fieldName;
    private final @NotNull PsiElement name;

    ROSMsgNameAnnotator(@NotNull AnnotationHolder holder,
                        @NotNull ROSMsgLabel name,
                        @NotNull String fieldName) {
        this.holder = holder;
        this.fieldName = fieldName;
        this.name = name;
    }

    void annDuplicateName() {
        int separatorCount = ROSMsgUtil.countNameInFile(name.getContainingFile(),fieldName);
        if (separatorCount > 1 && !ROSMsgUtil.isFirstDefinition(name.getContainingFile(),(ROSMsgField) name.getParent())) {
            TextRange range = new TextRange(name.getTextRange().getStartOffset(),
                    name.getTextRange().getEndOffset());
            Annotation ann = holder.createErrorAnnotation(range, "Field name '" + fieldName + "' is already used");
            ann.registerFix(new ChangeNameQuickFix((ROSMsgField) name.getParent(),name));
        }
    }

    void annIllegalName() {
        String regex = "[a-zA-Z][a-zA-Z0-9_]*";
        if(!fieldName.matches(regex)) {
            TextRange range = new TextRange(name.getTextRange().getStartOffset(),
                    name.getTextRange().getEndOffset());
            String message;
            if(fieldName.matches("[a-zA-Z0-9_]+")) {
                message = "Field names must start with a letter, not a number or underscore";
            } else {
                message = "Field names may only contain alphanumeric characters or underscores";
            }
            Annotation ann = holder.createErrorAnnotation(range, message);
            ann.registerFix(new ChangeNameQuickFix((ROSMsgField) name.getParent(),name));
        }
    }

    //TODO: a warning which checks for snake_case in names. make sure to check JB-inspection first
    //TODO: fix: offer conversion to snake_case
}
