package ros.integrate.pkt.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import ros.integrate.pkt.psi.ROSPktSeparator;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Removes all service separator lines from the file</p>
 * <p>this means that every "---" line in the file is deleted, even if the file requires at least one of these.</p>
 * <p>In action and service files, you will need to add the necessary number of separators back</p>
 * @author Noam Dori
 */
public class RemoveAllSrvLinesQuickFix extends BaseIntentionAction {

    @NotNull
    @Override
    public String getText() {
        return "Remove ALL service separators";
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
                WriteCommandAction.writeCommandAction(project).run(() -> {
                    for (PsiElement element : file.getChildren()) {
                        if(element instanceof ROSPktSeparator) {
                            element.delete();
                    }
                    }
                }));
    }
}