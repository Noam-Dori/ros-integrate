package ros.integrate.pkt.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.psi.ROSPktFieldBase;

/**
 * an intention that removes an entire field from a packet file.
 * @author Noam Dori
 */
public class RemoveFieldQuickFix extends BaseIntentionAction {

    /**
     * construct a new intention
     * @param field the field to delete
     */
    public RemoveFieldQuickFix(ROSPktFieldBase field) {
        this.field = field;
    }

    private final ROSPktFieldBase field;

    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS Message/Service errors";
    }

    @NotNull
    @Override
    public String getText() {
        return "Remove field " + (field.getLabel() != null ? "'" + field.getLabel().getText() + "'" : " fragment");
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) {
        ApplicationManager.getApplication().invokeLater(() ->
                WriteCommandAction.writeCommandAction(project).run(field::delete));
    }
}
