package ros.integrate.msg.annotate;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import ros.integrate.msg.intention.RemoveAllSrvLinesQuickFix;
import ros.integrate.msg.intention.RemoveSrvLineQuickFix;
import ros.integrate.msg.psi.ROSFile;
import ros.integrate.msg.psi.ROSMsgSeparator;

/**
 * an annotator dedicated to {@link ROSMsgSeparator}
 */
class ROSMsgSeparatorAnnotator extends ROSMsgAnnotatorBase {
    private final ROSMsgSeparator sep;

    ROSMsgSeparatorAnnotator(@NotNull AnnotationHolder holder, @NotNull ROSMsgSeparator sep) {
        super(holder);
        this.sep = sep;
    }

    /**
     * annotators if there are too many separators in the file provided.
     */
    void annTooManySeparators() {
        ROSFile file = (ROSFile) sep.getContainingFile();
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
