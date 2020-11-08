package ros.integrate.pkt.annotate;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationBuilder;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
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
 * an annotator dedicated to {@link ROSPktTypeBase}. This is the data type of the field. This can have multiple components
 * and uses a class-like concept
 * @author Noam Dori
 */
public class ROSPktTypeAnnotator extends ROSPktAnnotatorBase {
    private final String msgName;
    private final ROSPktTypeBase type;

    /**
     * construct the annotator
     *
     * @param holder  the annotation holder
     * @param type    the pointer to the PSI element containing the type
     * @param msgName the name of the file this type is in
     */
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
        if (type.raw().getText().equals(msgName)) {
            holder.newAnnotation(HighlightSeverity.ERROR, "A message cannot contain itself")
                    .range(type.raw().getTextRange())
                    .withFix(new RemoveFieldQuickFix((ROSPktFieldBase) type.getParent()))
                    .create();
        }
    }

    /**
     * annotates if the provided type is not defined anywhere.
     *
     * @return true if the annotation was activated, false if not.
     */
    boolean annTypeNotDefined() {
        if (unknownType()) {
            if (type.raw().getText().equals("Header")) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Header types must be prefixed with \"std_msgs/\" if they are not the first field")
                        .range(type.raw().getTextRange())
                        .highlightType(ProblemHighlightType.ERROR)
                        .withFix(new ChangeHeaderQuickFix(type))
                        .create();
            } else {
                holder.newAnnotation(HighlightSeverity.ERROR, "Unresolved message object")
                        .range(type.raw().getTextRange())
                        .highlightType(ProblemHighlightType.ERROR)
                        .withFix(new AddROSMsgQuickFix(type.raw()))
                        .create();
            }
            return true;
        }
        return false;
    }

    /**
     * does the test for {@link ROSPktTypeAnnotator#annTypeNotDefined()}
     *
     * @return true if the type is not defined anywhere, false otherwise.
     */
    private boolean unknownType() {
        Pair<String, String> name = getFullName();
        ROSMsgFile message = ROSPktUtil.findMessage(type.getProject(), name.first, name.second);
        return message == null && // found no message matching this field type.
                !(type.raw().getText().equals("Header") && type.getParent().equals(getFirstField())); // field is the header
    }

    @NotNull
    @Contract(" -> new")
    private Pair<String, String> getFullName() {
        String msg = type.raw().getText(), pkg;
        if (msg.contains("/")) {
            pkg = msg.replaceAll("/.*", "");
            msg = msg.replaceAll(".*/", "");
        } else {
            pkg = ((ROSPktFile) type.getContainingFile().getOriginalFile()).getPackage().getName();
        }
        return new Pair<>(pkg, msg);
    }

    /**
     * fetches the first field available in the section.
     *
     * @return null is no field is present, otherwise the first field available.
     */
    @Nullable
    private ROSPktFieldBase getFirstField() {
        List<ROSPktFieldBase> result = type.getContainingSection().getFields(ROSPktFieldBase.class, false);
        return result.isEmpty() ? null : result.get(0);
    }

    /**
     * annotates if a constant is present for an array type.
     *
     * @param isRemoveIntentionActive true if the remove intention is already active, false if not.
     * @param constant                the constant this type is tied to.
     * @return true if the annotation was activated, false if not.
     */
    @SuppressWarnings("SameParameterValue")
    boolean annArrayConst(boolean isRemoveIntentionActive, @NotNull ROSPktConst constant) {
        if (type.size() != -1) {
            TextRange range = constant.getTextRange();
            AnnotationBuilder ann = holder.newAnnotation(HighlightSeverity.ERROR, "Array fields cannot be assigned a constant.")
                    .range(range);
            if (!isRemoveIntentionActive) {
                ann = ann.withFix(new RemoveConstQuickFix((ROSPktFieldBase) type.getParent())); // remove const
            }
            ann.withFix(new RemoveArrayQuickFix(type)).create(); // remove arr
            return true;
        }
        return false;
    }

    /**
     * annotates if a constant is present for a type which does not support constants.
     *
     * @param isRemoveIntentionActive true if the remove intention is already active, false if not.
     * @param constant                the constant this type is tied to.
     * @return true if the annotation was activated, false if not.
     */
    boolean annBadTypeConst(boolean isRemoveIntentionActive, @NotNull ROSPktConst constant) {
        String numericalRegex = "(bool)|(string)|((uint|int)(8|16|32|64))|(float(32|64))|(byte)|(char)";
        if (!type.raw().getText().matches(numericalRegex)) { // a very simple regex
            TextRange range = constant.getTextRange();
            AnnotationBuilder ann = holder.newAnnotation(HighlightSeverity.ERROR, "Only the built-in string and numerical types may be assigned a constant.")
                    .range(range);
            if (!isRemoveIntentionActive) {
                ann = ann.withFix(new RemoveConstQuickFix((ROSPktFieldBase) type.getParent())); // remove const
            }
            ann.withFix(new ChangeKeytypeQuickFix(type, constant)).create(); // change type
            return true;
        }
        return false;
    }

    /**
     * annotates if the constant tied to this type cannot be held with the given memory restrictions this key-type has.
     *
     * @param isBadType a perquisite making sure the type is a valid key-type
     * @param constant  the constant this type is tied to.
     */
    void annConstTooBig(boolean isBadType, @NotNull ROSPktConst constant) {
        if (!isBadType && !((ROSPktFieldBase) type.getParent()).isLegalConstant()) {
            TextRange range = constant.getTextRange();
            holder.newAnnotation(HighlightSeverity.ERROR, "The constant's value cannot fit within the given type.")
                    .range(range)
                    .withFix(new ChangeKeytypeQuickFix(type, constant)) // change type
                    .create();
        }
    }

    /**
     * annotates if this type does not follow naming rules (alphanumeric,starts with letter)
     */
    void annIllegalType() {
        String message = getIllegalTypeMessage(type.raw().getText(), false);
        if (message != null) {
            holder.newAnnotation(HighlightSeverity.ERROR, message)
                    .range(type.raw().getTextRange())
                    .withFix(new RenameTypeQuickFix(null, "type"))
                    .create();
        }
    }

    /**
     * fetches the message for the illegal type if available.
     *
     * @param fieldType the name of the type to check.
     * @param inProject whether or not this type was defined in the project.
     * @return null if the type is fine, otherwise the reason this type is illegal.
     */
    @Nullable
    public static String getIllegalTypeMessage(@NotNull String fieldType, boolean inProject) {
        if (fieldType.equals("")) {
            return "Field types cannot be empty";
        }
        if (!fieldType.matches("[a-zA-Z][a-zA-Z0-9_]*" + (inProject ? "" : "/?") + "[a-zA-Z0-9_]*")) {
            if (fieldType.matches("[0-9/_].*")) {
                return "Field types must start with a letter";
            } else if (fieldType.matches(".*/.*" + (inProject ? "" : "/" + ".*"))) {
                return inProject ? "Project messages cannot be placed within packages"
                        : "Field types may use 1 slash at most";
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
        if (!type.isComplete()) { // a shortcut to skip all tests
            checkBadArray();
        }
    }

    private void checkBadArray() {
        if (type.getNode().getFirstChildNode().findChildByType(ROSPktTypes.RBRACKET) == null &&
                type.getNode().findChildByType(ROSPktTypes.RBRACKET) == null) {
            int typeEndOffset = type.getTextRange().getEndOffset();
            AnnotationBuilder ann = holder.newAnnotation(HighlightSeverity.ERROR, "']' expected");
            if (((ROSPktFieldBase)type.getParent()).getLabel() != null) {
                ann = ann.range(new TextRange(typeEndOffset, typeEndOffset + 1));
            }
            else
            {
                ann = ann.afterEndOfLine();
            }
            ann.create();
        }
    }
}