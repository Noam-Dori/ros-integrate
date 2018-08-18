package ros.integrate.msg.annotate;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import ros.integrate.msg.ROSMsgUtil;
import ros.integrate.msg.intention.*;
import ros.integrate.msg.psi.ROSMsgConst;
import ros.integrate.msg.psi.ROSMsgProperty;
import ros.integrate.msg.psi.ROSMsgTypes;

import java.util.List;
import java.util.Objects;

class ROSMsgTypeAnnotator {
    private final AnnotationHolder holder;
    private final Project project;
    private final ROSMsgProperty prop;
    private final String msgName;
    private String fieldType;

    ROSMsgTypeAnnotator(@NotNull AnnotationHolder holder,
                        @NotNull Project project,
                        @NotNull ROSMsgProperty prop,
                        @NotNull String msgName) {
        this.holder = holder;
        this.project = project;
        this.prop = prop;
        this.msgName = msgName;
    }

    void setFieldType(@NotNull String fieldType) {
        this.fieldType = fieldType;
    }

    void annSelfContaining() {
        // self containing msg inspection
        if(fieldType.equals(msgName)) {
            TextRange range = new TextRange(prop.getTextRange().getStartOffset(),
                    prop.getTextRange().getStartOffset() + fieldType.length());
            holder.createErrorAnnotation(range, "A message cannot contain itself")
                    .registerFix(new RemovePropertyQuickFix(prop));
        }
    }

    void annTypeNotDefined() {
        // type search
        // TODO: Search outside project (include files) for 'slashed' msgs
        // TODO: if catkin is defined, use it to search for msgs.
        List<String> types = ROSMsgUtil.findProjectMsgNames(project, fieldType,
                prop.getContainingFile().getVirtualFile());
        if (types.size() == 0 && // if we need special annotation for headers, then sure.
                !(fieldType.equals("Header") && prop.getNode()
                        .equals(prop.getContainingFile().getNode().findChildByType(ROSMsgTypes.PROPERTY))) &&
                !fieldType.contains("/") && !fieldType.equals(msgName)) {
            TextRange range = new TextRange(prop.getTextRange().getStartOffset(),
                    prop.getTextRange().getStartOffset() + fieldType.length());
            if(fieldType.equals("Header")) {
                Annotation ann = holder.createErrorAnnotation(range,
                        "Header types must be prefixed with 'std_msgs/' if they are not the first field");
                ann.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
                ann.registerFix(new ChangeHeaderQuickFix(prop));
            } else {
                Annotation ann = holder.createErrorAnnotation(range, "Unresolved message object");
                ann.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
                ann.registerFix(new AddROSMsgQuickFix(
                        Objects.requireNonNull(prop.getNode().findChildByType(ROSMsgTypes.TYPE)).getPsi())
                );
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    boolean annArrayConst(boolean isRemoveIntentionActive, @NotNull String constant) {
        if(prop.getArraySize() != -1) {
            int start = Objects.requireNonNull(prop.getNode().findChildByType(ROSMsgTypes.CONST)).getStartOffset();
            TextRange range = new TextRange(start, start + constant.length());
            Annotation ann = holder.createErrorAnnotation(range, "Array fields cannot be assigned a constant.");
            if(!isRemoveIntentionActive) {
                ann.registerFix(new RemoveConstQuickFix(prop)); // remove const
            }
            ann.registerFix(new RemoveArrayQuickFix(prop)); // remove arr
            return true;
        }
        return false;
    }

    boolean annBadTypeConst(boolean isRemoveIntentionActive, @NotNull String constant) {
        String numericalRegex = "(bool)|(string)|((uint|int)(8|16|32|64))|(float(32|64))";
        if(!fieldType.matches(numericalRegex)) { // a very simple regex
            int start = Objects.requireNonNull(prop.getNode().findChildByType(ROSMsgTypes.CONST)).getStartOffset();
            TextRange range = new TextRange(start, start + constant.length());
            Annotation ann = holder.createErrorAnnotation(range, "Only the built-in string and numerical types may be assigned a constant.");
            if(!isRemoveIntentionActive) {
                ann.registerFix(new RemoveConstQuickFix(prop)); // remove const
            }
            ann.registerFix(new ChangeKeytypeQuickFix(prop)); // change type
            return true;
        }
        return false;
    }

    void annConstTooBig(boolean isBadType, @NotNull String constant) {
        if(!isBadType && !prop.canHandle((ROSMsgConst)
                Objects.requireNonNull(prop.getNode().findChildByType(ROSMsgTypes.CONST)).getPsi())) {
            int start = Objects.requireNonNull(prop.getNode().findChildByType(ROSMsgTypes.CONST)).getStartOffset();
            TextRange range = new TextRange(start, start + constant.length());
            Annotation ann = holder.createErrorAnnotation(range, "The constant's value cannot fit within the given type.");
            ann.registerFix(new ChangeKeytypeQuickFix(prop)); // change type
        }
    }

    //TODO: msg types must follow the pattern [a-zA-Z][a-zA-Z0-9_]*/?[a-zA-Z0-9_]*
    void annIllegalType() {

    }
}
