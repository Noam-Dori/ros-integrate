package ros.integrate.pkt.annotate;

import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;

/**
 * a template class for ROS message annotators.
 *
 * Annotators are split based on what element they mark if something goes wrong.
 * The object created by annotators is known as an annotation.
 */
abstract class ROSPktAnnotatorBase {
    final @NotNull AnnotationHolder holder;

    ROSPktAnnotatorBase(@NotNull AnnotationHolder holder) {
        this.holder = holder;
    }
}
