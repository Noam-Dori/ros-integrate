package ros.integrate.pkt.annotate;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.intention.RemoveAllSrvLinesQuickFix;
import ros.integrate.pkt.intention.RemoveSrvLineQuickFix;
import ros.integrate.pkt.psi.ROSPktFile;
import ros.integrate.pkt.psi.ROSPktSeparator;

/**
 * an annotator dedicated to {@link ROSPktSeparator}
 */
class ROSPktSeparatorAnnotator extends ROSPktAnnotatorBase {
    private final ROSPktSeparator sep;

    ROSPktSeparatorAnnotator(@NotNull AnnotationHolder holder, @NotNull ROSPktSeparator sep) {
        super(holder);
        this.sep = sep;
    }

    /**
     * annotators if there are too many separators in the file provided.
     */
    void annTooManySeparators() {
        ROSPktFile file = (ROSPktFile) sep.getContainingFile();
        int separatorCount = file.countServiceSeparators();
        if (separatorCount > file.getMaxSeparators()) {
            TextRange range = new TextRange(sep.getTextRange().getStartOffset(),
                    sep.getTextRange().getEndOffset());
            Annotation ann = holder.createErrorAnnotation(range, file.getTooManySeparatorsMessage());
            ann.registerFix(new RemoveSrvLineQuickFix(sep));
            if(file.flagRemoveAll(separatorCount)) {ann.registerFix(new RemoveAllSrvLinesQuickFix());}
        }
    }
}
