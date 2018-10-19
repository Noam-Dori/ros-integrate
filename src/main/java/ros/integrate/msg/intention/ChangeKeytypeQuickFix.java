package ros.integrate.msg.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ros.integrate.msg.psi.ROSPktConst;
import ros.integrate.msg.psi.ROSPktType;

/**
 * a fix used to change the key-type according to the {@link ROSPktConst} provided.
 */
public class ChangeKeytypeQuickFix extends BaseIntentionAction {

    private final @NotNull
    ROSPktType type;
    private final @NotNull
    ROSPktConst constant;

    public ChangeKeytypeQuickFix(@NotNull ROSPktType type, @NotNull ROSPktConst constant) {
        this.type = type;
        this.constant = constant;
    }

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
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) {
        ApplicationManager.getApplication().invokeLater(() ->
                WriteCommandAction.writeCommandAction(project).run(() -> {
                    ROSPktType value = constant.getBestFit();
                    type.raw().replace(value);
                    Caret caret = editor.getCaretModel().getCurrentCaret();
                    caret.moveToOffset(type.getTextOffset());
                    caret.moveCaretRelatively(value.getTextLength(), 0, true, false);
                }));
    }
}
