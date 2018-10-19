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
 * a fix used to delete all service lines in a file
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