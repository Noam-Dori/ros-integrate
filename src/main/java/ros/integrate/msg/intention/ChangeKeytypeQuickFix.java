package ros.integrate.msg.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import ros.integrate.msg.ROSMsgUtil;
import ros.integrate.msg.psi.ROSMsgConst;
import ros.integrate.msg.psi.ROSMsgProperty;
import ros.integrate.msg.psi.ROSMsgTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ChangeKeytypeQuickFix extends BaseIntentionAction {

    public ChangeKeytypeQuickFix(ROSMsgProperty field) {
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
        return "Change field type";
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
                    type = type == null ? rosMsg.getNode().findChildByType(ROSMsgTypes.KEYTYPE) : type;
                    PsiElement value = ROSMsgUtil.getBestFit(
                            (ROSMsgConst) Objects.requireNonNull(rosMsg.getNode().findChildByType(ROSMsgTypes.CONST)).getPsi()
                    );
                    if (value != null && type != null) {
                        type.getPsi().replace(value);
                        int offset = rosMsg.getTextOffset();
                        Caret caret = editor.getCaretModel().getCurrentCaret();
                        caret.moveToOffset(offset);
                        caret.moveCaretRelatively(value.getTextLength(),0,true,false);
                    }
                }));
    }
}
