package ros.integrate.pkt.annotate;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.psi.ROSPktFieldBase;
import ros.integrate.pkt.psi.ROSPktTypes;

import java.util.Objects;

/**
 * a facade class used to annotate fields in packet files (.msg, .srv, .action).
 * Fields are basically one line, or one object the message type contains
 * @author Noam Dori
 */
class ROSPktFieldAnnotator extends ROSPktAnnotatorBase {
    @NotNull
    private final ROSPktFieldBase field;

    /**
     * construct the annotator
     *
     * @param holder the annotation holder
     * @param field  the field being checked and annotated.
     */
    ROSPktFieldAnnotator(AnnotationHolder holder, @NotNull ROSPktFieldBase field) {
        super(holder);
        this.field = field;
    }

    /**
     * annotates if the field either:
     * <ul>
     *     <li>is missing the name part</li>
     *     <li>has a const assignment, but no actual constant</li>
     *     <li>has an extra constant, but no '='</li>
     * </ul>
     * This is a multi-annotator, that is, it makes multiple annotations.
     */
    void annBadStructure() {
        if (!field.isComplete()) { // a shortcut to skip all tests
            checkMissingLabel();
            checkConst();
        }
    }

    private void checkConst() {
        ASTNode equalAST = field.getNode().findChildByType(ROSPktTypes.CONST_ASSIGNER);
        boolean equalSign = equalAST != null,
                constant = field.getConst() != null;
        if (equalSign && !constant) {
            int equalSignOffset = equalAST.getTextRange().getEndOffset();
            holder.newAnnotation(HighlightSeverity.ERROR, "Expected a constant value for the constant field")
                    .range(new TextRange(equalSignOffset, equalSignOffset + 1))
                    .create();
        }
        if (!equalSign && constant) {
            int labelEndOffset = Objects.requireNonNull(field.getLabel()).getTextRange().getEndOffset();
            holder.newAnnotation(HighlightSeverity.ERROR, "'=' expected")
                    .range(new TextRange(labelEndOffset, labelEndOffset + 1))
                    .create();
        }
    }

    private void checkMissingLabel() {
        if (field.getLabel() == null) {
            int typeEndOffset = field.getTypeBase().getTextRange().getEndOffset();
            holder.newAnnotation(HighlightSeverity.ERROR, "Expected a name for the field")
                    .range(new TextRange(typeEndOffset, typeEndOffset + 1))
                    .create();
        }
    }
}
