package ros.integrate.msg.annotate;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ros.integrate.msg.ROSMsgUtil;
import ros.integrate.msg.intention.RemoveFieldQuickFix;
import ros.integrate.msg.psi.ROSMsgProperty;
import ros.integrate.msg.psi.ROSMsgTypes;

import java.util.Objects;

class ROSMsgNameAnnotator {

    private final @NotNull AnnotationHolder holder;
    private final @NotNull ROSMsgProperty prop;
    private final @NotNull String fieldName;

    ROSMsgNameAnnotator(@NotNull AnnotationHolder holder,
                        @NotNull ROSMsgProperty prop,
                        @NotNull String fieldName) {
        this.holder = holder;
        this.prop = prop;
        this.fieldName = fieldName;
    }

    void annDuplicateName() {
        int separatorCount = ROSMsgUtil.countNameInFile(prop.getContainingFile(),fieldName);
        if (separatorCount > 1 && !ROSMsgUtil.isFirstDefinition(prop.getContainingFile(),prop)) {
            PsiElement name = Objects.requireNonNull(prop.getNode().findChildByType(ROSMsgTypes.NAME)).getPsi();
            TextRange range = new TextRange(name.getTextRange().getStartOffset(),
                    name.getTextRange().getEndOffset());
            Annotation ann = holder.createErrorAnnotation(range, "Field name '" + name.getText() + "' is already used");
            ann.registerFix(new RemoveFieldQuickFix(prop));
        }
    }

    //TODO: ROS Msgs must start with a-zA-Z and may only use a-zA-z0-9
    //TODO: fix: rename field
    void annIllegalName() {

    }

    //TODO: a warning which checks for snake_case in names. make sure to check JB-inspection first
    //TODO: fix: offer conversion to snake_case
}
