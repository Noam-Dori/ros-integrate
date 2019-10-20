package ros.integrate.pkt.annotate;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.psi.ROSPktFieldBase;
import ros.integrate.pkt.psi.ROSPktTypes;

import java.util.Objects;

class ROSPktFieldAnnotator extends ROSPktAnnotatorBase {
    @NotNull
    private final ROSPktFieldBase field;

    ROSPktFieldAnnotator(AnnotationHolder holder, @NotNull ROSPktFieldBase field) {
        super(holder);
        this.field = field;
    }

    /**
     * annotates if the field (NOT the type) has bad structure.
     * This is a multi-annotator, that is, it makes multiple annotations.
     */
    void annBadStructure() {
        if(!field.isComplete()) { // a shortcut to skip all tests
            checkMissingLabel();
            checkConst();
        }
    }

    private void checkConst() {
        ASTNode equalAST = field.getNode().findChildByType(ROSPktTypes.CONST_ASSIGNER);
        boolean equalSign = equalAST != null,
                constant = field.getConst() != null;
        if(equalSign && !constant) {
            int equalSignOffset = equalAST.getTextRange().getEndOffset();
            holder.createErrorAnnotation(new TextRange(equalSignOffset, equalSignOffset + 1), "expected a constant value for the constant field");
        }
        if (!equalSign && constant) {
            int labelEndOffset = Objects.requireNonNull(field.getLabel()).getTextRange().getEndOffset();
            holder.createErrorAnnotation(new TextRange(labelEndOffset, labelEndOffset + 1), "'=' expected");
        }
    }

    private void checkMissingLabel() {
        if(field.getLabel() == null) {
            int typeEndOffset = field.getTypeBase().getTextRange().getEndOffset();
            holder.createErrorAnnotation(new TextRange(typeEndOffset, typeEndOffset + 1), "Expected a name for the field");
        }
    }
}
