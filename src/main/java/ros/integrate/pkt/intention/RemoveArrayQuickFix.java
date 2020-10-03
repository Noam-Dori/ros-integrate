package ros.integrate.pkt.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.psi.ROSPktTypeBase;

/**
 * an intention that removes the array modifier from an array type in a field in packet files.
 * @author Noam Dori
 */
public class RemoveArrayQuickFix extends BaseIntentionAction {

    /**
     * construct a new intention
     * @param type the type to modify
     */
    public RemoveArrayQuickFix(ROSPktTypeBase type) {
        this.type = type;
    }

    private final ROSPktTypeBase type;

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
                WriteCommandAction.writeCommandAction(project).run(type::removeArray));
    }
}
