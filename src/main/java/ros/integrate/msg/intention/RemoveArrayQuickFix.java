package ros.integrate.msg.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ros.integrate.msg.psi.ROSMsgType;

public class RemoveArrayQuickFix extends BaseIntentionAction {

    public RemoveArrayQuickFix(ROSMsgType type) {
        this.type = type;
    }

    private ROSMsgType type;

    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS Message/Service errors";
    }

    @NotNull
    @Override
    public String getText() {
        return "Remove array";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) {
        ApplicationManager.getApplication().invokeLater(() ->
                WriteCommandAction.writeCommandAction(project).run(() -> type.removeArray()));
    }
}
