package ros.integrate.msg.annotate;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import ros.integrate.msg.intention.RemoveAllSrvLinesQuickFix;
import ros.integrate.msg.intention.RemoveSrvLineQuickFix;
import ros.integrate.msg.psi.ROSMsgFile;
import ros.integrate.msg.psi.ROSMsgSeparator;

class ROSMsgSeparatorAnnotator {
    private final AnnotationHolder holder;
    private final ROSMsgSeparator sep;
    private final @NotNull ROSMsgFile file;

    ROSMsgSeparatorAnnotator(@NotNull AnnotationHolder holder, @NotNull ROSMsgSeparator sep) {
        this.holder = holder;
        this.sep = sep;
        this.file = (ROSMsgFile) sep.getContainingFile();
    }

    void annTooManySeparators(int max, String message) {
        int separatorCount = file.countServiceSeparators();
        if (separatorCount > max) { // in Srv files this is 1
            TextRange range = new TextRange(sep.getTextRange().getStartOffset(),
                    sep.getTextRange().getEndOffset());
            Annotation ann = holder.createErrorAnnotation(range, message);
            ann.registerFix(new RemoveSrvLineQuickFix(sep));
            if(separatorCount > 1) {ann.registerFix(new RemoveAllSrvLinesQuickFix());}
        }
    }
}
