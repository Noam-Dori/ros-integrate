package ros.integrate.msg.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import ros.integrate.msg.psi.ROSMsgElementFactory;
import ros.integrate.msg.psi.ROSMsgProperty;
import ros.integrate.msg.psi.ROSMsgTypes;
import org.jetbrains.annotations.NotNull;

public class ChangeHeaderQuickFix extends BaseIntentionAction {

    public ChangeHeaderQuickFix(ROSMsgProperty field) {
        rosMsg = field;
    }

    private ROSMsgProperty rosMsg;

    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS Message/Service errors";
    }

    @NotNull
    @Override
    public String getText() {
        return "Add prefix to header";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file)
            throws IncorrectOperationException {
        ApplicationManager.getApplication().invokeLater(() ->
                WriteCommandAction.writeCommandAction(project).run(() -> {
                    ASTNode type = rosMsg.getNode().findChildByType(ROSMsgTypes.TYPE);
                    if (type != null) {
                        type.getPsi().replace(ROSMsgElementFactory.createType(project,"std_msgs/Header"));
                    }
                }));
    }
}
