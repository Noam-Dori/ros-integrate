package ros.integrate.msg.annotate;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.msg.ROSMsgUtil;
import ros.integrate.msg.intention.*;
import ros.integrate.msg.psi.ROSMsgConst;
import ros.integrate.msg.psi.ROSMsgField;
import ros.integrate.msg.psi.ROSMsgType;
import ros.integrate.msg.psi.ROSMsgTypes;

import java.util.List;

public class ROSMsgTypeAnnotator {
    private final AnnotationHolder holder;
    private final Project project;
    private final String msgName;
    private ROSMsgType type;

    ROSMsgTypeAnnotator(@NotNull AnnotationHolder holder,
                        @NotNull Project project,
                        @NotNull ROSMsgType type,
                        @NotNull String msgName) {
        this.holder = holder;
        this.project = project;
        this.type = type;
        this.msgName = msgName;
    }

    void annSelfContaining() {
        // self containing msg inspection
        if(type.raw().getText().equals(msgName)) {
            holder.createErrorAnnotation(type.raw().getTextRange(), "A message cannot contain itself")
                    .registerFix(new RemoveFieldQuickFix((ROSMsgField) type.getParent()));
        }
    }

    boolean annTypeNotDefined() {
        // type search
        // TODO: Search outside project (include files) for 'slashed' msgs <CLION>
        // TODO: if catkin is defined, use it to search for msgs. <CLION>
        if (unknownType()) {
            if(type.raw().getText().equals("Header")) {
                Annotation ann = holder.createErrorAnnotation(type.raw().getTextRange(),
                        "Header types must be prefixed with \"std_msgs/\" if they are not the first field");
                ann.setHighlightType(ProblemHighlightType.ERROR);
                ann.registerFix(new ChangeHeaderQuickFix(type));
            } else {
                Annotation ann = holder.createErrorAnnotation(type.raw().getTextRange(), "Unresolved message object");
                ann.setHighlightType(ProblemHighlightType.ERROR);
                ann.registerFix(new AddROSMsgQuickFix(type.raw()));
            }
            return true;
        }
        return false;
    }

    private boolean unknownType() {
        List<String> types = ROSMsgUtil.findProjectMsgNames(project, type.raw().getText(), null);
        return types.isEmpty() && // found no message within project matching this field type.
                !(type.raw().getText().equals("Header") && type.getParent().getNode().equals(getFirstField())) && // field is the header
                !type.raw().getText().contains("/"); // message is defined outside project
    }

    private ASTNode getFirstField() {
        return type.getContainingFile().getNode().findChildByType(ROSMsgTypes.FIELD);
    }

    @SuppressWarnings("SameParameterValue")
    boolean annArrayConst(boolean isRemoveIntentionActive, @NotNull ROSMsgConst constant) {
        if(type.size() != -1) {
            TextRange range = constant.getTextRange();
            Annotation ann = holder.createErrorAnnotation(range, "Array fields cannot be assigned a constant.");
            if(!isRemoveIntentionActive) {
                ann.registerFix(new RemoveConstQuickFix((ROSMsgField) type.getParent())); // remove const
            }
            ann.registerFix(new RemoveArrayQuickFix(type)); // remove arr
            return true;
        }
        return false;
    }

    boolean annBadTypeConst(boolean isRemoveIntentionActive, @NotNull ROSMsgConst constant) {
        String numericalRegex = "(bool)|(string)|((uint|int)(8|16|32|64))|(float(32|64))";
        if (!type.raw().getText().matches(numericalRegex)) { // a very simple regex
            TextRange range = constant.getTextRange();
            Annotation ann = holder.createErrorAnnotation(range, "Only the built-in string and numerical types may be assigned a constant.");
            if (!isRemoveIntentionActive) {
                ann.registerFix(new RemoveConstQuickFix((ROSMsgField) type.getParent())); // remove const
            }
            ann.registerFix(new ChangeKeytypeQuickFix(type, constant)); // change type
            return true;
        }
        return false;
    }

    void annConstTooBig(boolean isBadType, @NotNull ROSMsgConst constant) {
        if(!isBadType && !((ROSMsgField) type.getParent()).isLegalConstant()) {
            TextRange range = constant.getTextRange();
            Annotation ann = holder.createErrorAnnotation(range, "The constant's value cannot fit within the given type.");
            ann.registerFix(new ChangeKeytypeQuickFix(type,constant)); // change type
        }
    }

    void annIllegalType() {
        String message = getIllegalTypeMessage(type.raw().getText(),false);
        if (message != null) {
            Annotation ann = holder.createErrorAnnotation(type.raw().getTextRange(), message);
            ann.registerFix(new RenameTypeQuickFix.RenameTypeIntention(type,message));
        }
    }

    @Nullable
    public static String getIllegalTypeMessage(@NotNull String fieldType, boolean inProject) {
        if(fieldType.equals("")) {return "Field types cannot be empty";}
        if(!fieldType.matches("[a-zA-Z][a-zA-Z0-9_]*" + (inProject ? "" : "/?") + "[a-zA-Z0-9_]*")) {
            if (fieldType.matches("[0-9/_].*")) {
                return "Field types must start with a letter, not a number, underscore, or slash";
            } else if (fieldType.matches(".*/.*"+ (inProject ? "" : "/" +".*"))) {
                return inProject ? "Project messages cannot be placed within packages"
                        : "Messages cannot have a sub-package, therefore field types may use only 1 slash";
            } else {
                return "Field types may only contain alphanumeric characters or underscores";
            }
        }
        return null;
    }
}
