package ros.integrate.pkt.inspection;

import com.intellij.codeInsight.daemon.HighlightDisplayKey;
import com.intellij.codeInsight.daemon.impl.actions.SuppressByCommentFix;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.psi.ROSPktFieldBase;

/**
 * a fix intended to suppress inspections via a comment.
 */
class SuppressFieldByCommentFix extends SuppressByCommentFix {
    SuppressFieldByCommentFix(@NotNull HighlightDisplayKey key) {
        super(key, ROSPktFieldBase.class);
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    @Override
    protected void createSuppression(@NotNull final Project project,
                                     @NotNull final PsiElement element,
                                     @NotNull final PsiElement container) {
        PsiElement declaredElement = getElementToAnnotate(container);
        if (declaredElement == null) {
            WriteCommandAction.runWriteCommandAction(project, null, null, () -> suppressWithComment(project, element, container), container.getContainingFile());
        }
        else {
            ROSPktSuppressionUtil.addSuppressAnnotation(project, container, myID);
        }
    }

    @Override
    protected boolean replaceSuppressionComments(@Nullable PsiElement container) {
        if (getElementToAnnotate(container) != null) return false;
        return super.replaceSuppressionComments(container);
    }

    @Override
    @NotNull
    public String getText() {
        return "Suppress for field";
    }

    /**
     * see {@link ROSPktSuppressionUtil#getElementToAnnotate(PsiElement)}
     */
    @Contract(value = "null -> null", pure = true)
    private PsiElement getElementToAnnotate(@Nullable PsiElement container) {
        return ROSPktSuppressionUtil.getElementToAnnotate(container);
    }

    /**
     * see {@link SuppressByCommentFix#createSuppression(Project, PsiElement, PsiElement)}
     */
    private void suppressWithComment(@NotNull Project project,@NotNull PsiElement element,@NotNull PsiElement container) {
        super.createSuppression(project, element, container);
    }
}
