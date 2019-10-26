package ros.integrate.pkt.annotate;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.ROSPktUtil;
import ros.integrate.pkt.intention.*;
import ros.integrate.pkt.psi.*;

import java.util.List;

/**
 * an annotator dedicated to {@link ROSPktTypeBase}
 */
public class ROSPktTypeAnnotator extends ROSPktAnnotatorBase {
    private final String msgName;
    private final ROSPktTypeBase type;

    ROSPktTypeAnnotator(@NotNull AnnotationHolder holder,
                        @NotNull ROSPktTypeBase type,
                        @NotNull String msgName) {
        super(holder);
        this.type = type;
        this.msgName = msgName;
    }

    /**
     * annotates if a message contains itself as a field (or fragment)
     */
    void annSelfContaining() {
        if(type.raw().getText().equals(msgName)) {
            holder.createErrorAnnotation(type.raw().getTextRange(), "A message cannot contain itself")
                    .registerFix(new RemoveFieldQuickFix((ROSPktFieldBase) type.getParent()));
        }
    }

    /**
     * annotates if the provided type is not defined anywhere.
     * @return true if the annotation was activated, false if not.
     */
    boolean annTypeNotDefined() {
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

    /**
     * does the test for {@link ROSPktTypeAnnotator#annTypeNotDefined()}
     * @return true if the type is not defined anywhere, false otherwise.
     */
    private boolean unknownType() {
        Pair<String,String> name = getFullName();
        ROSMsgFile message = ROSPktUtil.findMessage(type.getProject(), name.first, name.second );
        return message == null && // found no message matching this field type.
                !(type.raw().getText().equals("Header") && type.getParent().equals(getFirstField())); // field is the header
    }

    @NotNull
    @Contract(" -> new")
    private Pair<String,String> getFullName() {
        String msg = type.raw().getText(), pkg;
        if(msg.contains("/")) {
            pkg = msg.replaceAll("/.*","");
            msg = msg.replaceAll(".*/","");
        } else {
            pkg = ((ROSPktFile)type.getContainingFile().getOriginalFile()).getPackage().getName();
        }
        return new Pair<>(pkg,msg);
    }

    /**
     * fetches the first field available in the section.
     * @return null is no field is present, otherwise the first field available.
     */
    @Nullable
    private ROSPktFieldBase getFirstField() {
        List<ROSPktFieldBase> result = type.getContainingSection().getFields(ROSPktFieldBase.class, false);
        return result.isEmpty() ? null : result.get(0);
    }

    /**
     * annotates if a constant is present for an array type.
     * @param isRemoveIntentionActive true if the remove intention is already active, false if not.
     * @param constant the constant this type is tied to.
     * @return true if the annotation was activated, false if not.
     */
    @SuppressWarnings("SameParameterValue")
    boolean annArrayConst(boolean isRemoveIntentionActive, @NotNull ROSPktConst constant) {
        if(type.size() != -1) {
            TextRange range = constant.getTextRange();
            Annotation ann = holder.createErrorAnnotation(range, "Array fields cannot be assigned a constant.");
            if(!isRemoveIntentionActive) {
                ann.registerFix(new RemoveConstQuickFix((ROSPktFieldBase) type.getParent())); // remove const
            }
            ann.registerFix(new RemoveArrayQuickFix(type)); // remove arr
            return true;
        }
        return false;
    }

    /**
     * annotates if a constant is present for a type which does not support constants.
     * @param isRemoveIntentionActive true if the remove intention is already active, false if not.
     * @param constant the constant this type is tied to.
     * @return true if the annotation was activated, false if not.
     */
    boolean annBadTypeConst(boolean isRemoveIntentionActive, @NotNull ROSPktConst constant) {
        String numericalRegex = "(bool)|(string)|((uint|int)(8|16|32|64))|(float(32|64))";
        if (!type.raw().getText().matches(numericalRegex)) { // a very simple regex
            TextRange range = constant.getTextRange();
            Annotation ann = holder.createErrorAnnotation(range, "Only the built-in string and numerical types may be assigned a constant.");
            if (!isRemoveIntentionActive) {
                ann.registerFix(new RemoveConstQuickFix((ROSPktFieldBase) type.getParent())); // remove const
            }
            ann.registerFix(new ChangeKeytypeQuickFix(type, constant)); // change type
            return true;
        }
        return false;
    }

    /**
     * annotates if the constant tied to this type cannot be held with the given memory restrictions this key-type has.
     * @param isBadType a perquisite making sure the type is a valid key-type
     * @param constant the constant this type is tied to.
     */
    void annConstTooBig(boolean isBadType, @NotNull ROSPktConst constant) {
        if(!isBadType && !((ROSPktFieldBase) type.getParent()).isLegalConstant()) {
            TextRange range = constant.getTextRange();
            Annotation ann = holder.createErrorAnnotation(range, "The constant's value cannot fit within the given type.");
            ann.registerFix(new ChangeKeytypeQuickFix(type,constant)); // change type
        }
    }

    /**
     * annotates if this type does not follow naming rules (alphanumeric,starts with letter)
     */
    void annIllegalType() {
        String message = getIllegalTypeMessage(type.raw().getText(),false);
        if (message != null) {
            Annotation ann = holder.createErrorAnnotation(type.raw().getTextRange(), message);
            ann.registerFix(new RenameTypeQuickFix.RenameTypeIntention(type,message));
        }
    }

    /**
     * fetches the message for the illegal type if available.
     * @param fieldType the name of the type to check.
     * @param inProject whether or not this type was defined in the project.
     * @return null if the type is fine, otherwise the reason this type is illegal.
     */
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

    /**
     * annotates if the type has bad structure.
     * This is a multi-annotator, that is, it makes multiple annotations.
     */
    void annBadStructure() {
        if(!type.isComplete()) { // a shortcut to skip all tests
            checkBadArray();
        }
    }

    private void checkBadArray() {
        if(type.getNode().getFirstChildNode().findChildByType(ROSPktTypes.RBRACKET) == null &&
                type.getNode().findChildByType(ROSPktTypes.RBRACKET) == null) {
            int typeEndOffset = type.getTextRange().getEndOffset();
            holder.createErrorAnnotation(new TextRange(typeEndOffset,typeEndOffset + 1), "']' expected");
        }
    }
}