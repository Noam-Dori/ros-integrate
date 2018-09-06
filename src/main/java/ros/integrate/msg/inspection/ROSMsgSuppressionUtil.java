package ros.integrate.msg.inspection;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.msg.ROSMsgUtil;
import ros.integrate.msg.psi.ROSMsgComment;
import ros.integrate.msg.psi.ROSMsgElementFactory;
import ros.integrate.msg.psi.ROSMsgProperty;

public class ROSMsgSuppressionUtil {

    public static void addSuppressAnnotation(@NotNull Project project,
                                             final PsiElement container,
                                             @NotNull String id) throws IncorrectOperationException {
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

    private static void appendAnnotation(@NotNull Project project, @NotNull ROSMsgComment newAnnotation, @NotNull PsiElement container) {
        container.getContainingFile().addBefore(newAnnotation,container);
        container.getContainingFile().addBefore(ROSMsgElementFactory.createCRLF(project),container);
    }

    @Nullable
    private static ROSMsgComment createNewAnnotation(@NotNull Project project, ROSMsgComment annotation, @NotNull String id) {
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

    @Contract(value = "null -> null", pure = true)
    @Nullable
    public static PsiElement getElementToAnnotate(PsiElement container) {
        if (container instanceof ROSMsgProperty) {
            return container;
        }
        return null;
    }
}
