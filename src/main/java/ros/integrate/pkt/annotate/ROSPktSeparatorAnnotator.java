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
 * an annotator dedicated to {@link ROSPktSeparator}, used in .srv and .action files to distinguish between the sub-messages
 * request, response, and feedback in actions
 * @author Noam Dori
 */
class ROSPktSeparatorAnnotator extends ROSPktAnnotatorBase {
    private final ROSPktSeparator sep;

    /**
     * construct the annotator
     * @param holder the annotation holder
     * @param sep the separator being checked and annotated.
     */
    ROSPktSeparatorAnnotator(@NotNull AnnotationHolder holder, @NotNull ROSPktSeparator sep) {
        super(holder);
        this.sep = sep;
    }

    /**
     * annotators if there are too many separators in the file provided.
     */
    void annTooManySeparators() {
        ROSPktFile file = (ROSPktFile) sep.getContainingFile();
        int separatorCount = file.countSectionSeparators();
        if (separatorCount > file.getMaxSeparators()) {
            TextRange range = new TextRange(sep.getTextRange().getStartOffset(),
                    sep.getTextRange().getEndOffset());
            Annotation ann = holder.createErrorAnnotation(range, file.getTooManySeparatorsMessage());
            ann.registerFix(new RemoveSrvLineQuickFix(sep));
            if(file.flagRemoveAll(separatorCount)) {ann.registerFix(new RemoveAllSrvLinesQuickFix());}
        }
    }
}
