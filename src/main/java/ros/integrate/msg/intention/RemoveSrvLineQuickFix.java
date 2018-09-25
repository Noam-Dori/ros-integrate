package ros.integrate.msg.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import ros.integrate.msg.psi.ROSMsgSeparator;
import org.jetbrains.annotations.NotNull;

/**
 * a fix used to remove one service separator from the file.
 */
public class RemoveSrvLineQuickFix extends BaseIntentionAction {

    private ROSMsgSeparator separator;

    public RemoveSrvLineQuickFix(ROSMsgSeparator separator) {
        this.separator = separator;
    }

    @NotNull
    @Override
    public String getText() {
        return "Remove service separator";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS Message/Service errors";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull final Project project, final Editor editor, PsiFile file) throws
            IncorrectOperationException {
        ApplicationManager.getApplication().invokeLater(() ->
                WriteCommandAction.writeCommandAction(project).run(() -> separator.delete()));
    }
}
