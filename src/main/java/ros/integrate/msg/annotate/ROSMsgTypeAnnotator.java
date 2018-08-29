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
import ros.integrate.msg.psi.ROSMsgProperty;
import ros.integrate.msg.psi.ROSMsgTypes;

import java.util.List;
import java.util.Objects;

public class ROSMsgTypeAnnotator {
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
                    .registerFix(new RemoveFieldQuickFix(prop));
        }
    }

    void annTypeNotDefined() {
        // type search
        // TODO: Search outside project (include files) for 'slashed' msgs <CLION>
        // TODO: if catkin is defined, use it to search for msgs. <CLION>
        if (unknownType()) {
            TextRange range = new TextRange(prop.getTextRange().getStartOffset(),
                    prop.getTextRange().getStartOffset() + fieldType.length());
            if(fieldType.equals("Header")) {
                Annotation ann = holder.createErrorAnnotation(range,
                        "Header types must be prefixed with \"std_msgs/\" if they are not the first field");
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

    private boolean unknownType() {
        List<String> types = ROSMsgUtil.findProjectMsgNames(project, fieldType, null);
        return types.isEmpty() && // found no message within project matching this field type.
                !(fieldType.equals("Header") && prop.getNode().equals(getFirstProperty())) && // field is the header
                !fieldType.contains("/"); // message is defined outside project
    }

    private ASTNode getFirstProperty() {
        return prop.getContainingFile().getNode().findChildByType(ROSMsgTypes.PROPERTY);
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

    void annIllegalType() {
        String message = getIllegalTypeMessage(fieldType,false);
        if (message != null) {
            TextRange range = new TextRange(prop.getType().getTextRange().getStartOffset(),
                    prop.getType().getTextRange().getEndOffset());
            Annotation ann = holder.createErrorAnnotation(range, message);
            //ann.registerFix(new ChangeTypeQuickFix(prop,prop.getType()));
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

    @Nullable
    public static String getUnorthodoxTypeMessage(@NotNull String fieldType, boolean inProject) {
        String camelCase = "([A-Za-z]|([0-9]([A-Z]|$)))*";
        String regex = inProject ? "[A-Z]" + camelCase
                : "[a-zA-Z][a-zA-Z0-9_]*/?[A-Z]" + camelCase;
        if(!fieldType.matches(regex)) {
            return "Field types should be written in CamelCase";
        }
        return null;
    }
}
