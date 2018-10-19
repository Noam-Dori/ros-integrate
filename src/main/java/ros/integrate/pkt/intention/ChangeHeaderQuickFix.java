package ros.integrate.pkt.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.psi.ROSPktType;

/**
 * a fix used to change the header type as necessary.
 */
public class ChangeHeaderQuickFix extends BaseIntentionAction {

    public ChangeHeaderQuickFix(ROSPktType field) {
        header = field;
    }

    private ROSPktType header;

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
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) {
        ApplicationManager.getApplication().invokeLater(
                () -> WriteCommandAction.writeCommandAction(project).run(() -> header.set("std_msgs/Header",header.size())));
    }
}
