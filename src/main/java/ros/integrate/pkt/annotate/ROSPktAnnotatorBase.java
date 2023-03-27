package ros.integrate.pkt.annotate;

import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * a template class for ROS message annotators.
 * <p>
 * Annotators are split based on what element they mark if something goes wrong.
 * The object created by annotators is known as an annotation.
 * @author Noam Dori
 */
abstract class ROSPktAnnotatorBase {
    final @NotNull AnnotationHolder holder;

    /**
     * constructs a new annotator
     * @param holder the annotation holder.
     */
    @Contract(pure = true)
    ROSPktAnnotatorBase(@NotNull AnnotationHolder holder) {
        this.holder = holder;
    }
}
