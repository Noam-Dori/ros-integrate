package ros.integrate.msg.inspection;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.msg.ROSMsgUtil;
import ros.integrate.msg.psi.ROSMsgComment;
import ros.integrate.msg.psi.ROSMsgElementFactory;
import ros.integrate.msg.psi.ROSMsgField;

/**
 * a collection of utility functions regarding suppressing ROS inspections.
 */
class ROSMsgSuppressionUtil {

    /**
     * add a suppression annotation
     * @param project the project this is present in.
     * @param container the element containing the supposed annotation
     * @param id the string ID of the inspection
     */
    static void addSuppressAnnotation(@NotNull Project project,
                                      final @NotNull PsiElement container,
                                      @NotNull String id) {
        final ROSMsgComment annotation = findAnnotation(container);
        final ROSMsgComment newAnnotation = createNewAnnotation(project, annotation, id);
        if (newAnnotation != null) {
            if (annotation != null && annotation.isPhysical()) {
                WriteCommandAction.runWriteCommandAction(project, null, null, () -> annotation.replace(newAnnotation), annotation.getContainingFile());
            }
            else {
                WriteCommandAction.runWriteCommandAction(project, null, null, () -> appendAnnotation(project,newAnnotation,container), container.getContainingFile());
            }
        }
    }

    /**
     * fetches the annotation for the element if it exists.
     * @param container the element containing the supposed annotation
     * @return null if no annotation is available for this PSI element,
     *         otherwise an annotation in the form of a {@link ROSMsgComment}
     */
    @Nullable
    static ROSMsgComment findAnnotation(@NotNull PsiElement container) {
        PsiElement[] fields = container.getContainingFile().getChildren();
        int i = fields.length - 1;
        for (; i > 0; i--) {
            if (fields[i] == container) {
                break;
            }
        }
        for (i--; i >= 0; i--) {
            if (fields[i] instanceof ROSMsgComment) {
                return ROSMsgUtil.checkAnnotation(fields[i]);
            }
        }
        return null;
    }

    /**
     * adds an annotation to the element provided
     * @param project the project this object is present in.
     * @param newAnnotation the new annotation to add.
     * @param container the PSI element to add an annotation to.
     */
    private static void appendAnnotation(@NotNull Project project, @NotNull ROSMsgComment newAnnotation, @NotNull PsiElement container) {
        container.getContainingFile().addBefore(newAnnotation,container);
        container.getContainingFile().addBefore(ROSMsgElementFactory.createCRLF(project),container);
    }

    /**
     * generate a new suppression annotation.
     * @param project project the project this object is present in.
     * @param annotation null if the container has no previous annotation, otherwise the previous annotation.
     * @param id the id this annotation suppresses.
     * @return the new annotation to replace or append for the element provided
     */
    @Nullable
    private static ROSMsgComment createNewAnnotation(@NotNull Project project, @Nullable ROSMsgComment annotation, @NotNull String id) {
        if (annotation == null || annotation.getAnnotationIds() == null || annotation.getAnnotationIds().equals("")) {
            return ROSMsgElementFactory.createAnnotation(project, id);
        }
        final String prevIds = annotation.getAnnotationIds();
        if (prevIds != null) {
            if (prevIds.contains(id)) return null;
            return ROSMsgElementFactory.createAnnotation(project,prevIds + "," + id);
        }
        return null;
    }

    /**
     * fetches the element to annotate.
     * @param container the PSI element to add an annotation to.
     * @return null if the element provided cannot be annotated, otherwise returns itself.
     */
    @Contract(value = "null -> null", pure = true)
    static PsiElement getElementToAnnotate(@Nullable PsiElement container) {
        if (container instanceof ROSMsgField) {
            return container;
        }
        return null;
    }
}
