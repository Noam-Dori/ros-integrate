package ros.integrate.msg.inspection;

import com.intellij.codeInsight.daemon.HighlightDisplayKey;
import com.intellij.codeInsight.daemon.impl.actions.SuppressByCommentFix;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.msg.ROSMsgLanguage;
import ros.integrate.msg.psi.ROSMsgField;

public class SuppressFieldByCommentFix extends SuppressByCommentFix {
    public SuppressFieldByCommentFix(@NotNull HighlightDisplayKey key) {
        super(key, ROSMsgField.class);
    }

    public SuppressFieldByCommentFix(@NotNull String toolId) {
        super(toolId, ROSMsgField.class);
    }

    @Override
    @Nullable
    public PsiElement getContainer(PsiElement context) {
        ROSMsgField field = PsiTreeUtil.getParentOfType(context, ROSMsgField.class, false);
        return field != null && ROSMsgLanguage.INSTANCE.equals(field.getLanguage()) ? field : null;
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
            ROSMsgSuppressionUtil.addSuppressAnnotation(project, container, myID);
        }
    }

    @Override
    protected boolean replaceSuppressionComments(PsiElement container) {
        if (getElementToAnnotate(container) != null) return false;
        return super.replaceSuppressionComments(container);
    }

    @Override
    @NotNull
    public String getText() {
        return "Suppress for field";
    }

    @Nullable
    protected PsiElement getElementToAnnotate(PsiElement container) {
        return ROSMsgSuppressionUtil.getElementToAnnotate(container);
    }

    protected void suppressWithComment(Project project, PsiElement element, PsiElement container) {
        super.createSuppression(project, element, container);
    }
}
