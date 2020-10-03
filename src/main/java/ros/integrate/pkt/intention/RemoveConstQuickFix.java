package ros.integrate.pkt.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import ros.integrate.pkt.psi.ROSPktFieldBase;
import ros.integrate.pkt.psi.ROSPktTypes;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Removes the const value assignment from the field</p>
 * <p>This makes the field a property field instead of a const-value field</p>
 * @author Noam Dori
 */
public class RemoveConstQuickFix extends BaseIntentionAction {

    /**
     * construct a new intention
     * @param field the field to modify
     */
    public RemoveConstQuickFix(ROSPktFieldBase field) {
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
        return "Remove constant";
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
                    ASTNode start = field.getNode().findChildByType(ROSPktTypes.CONST_ASSIGNER),
                            end = field.getNode().findChildByType(ROSPktTypes.CONST);
                    if (start != null && end != null) {
                        field.deleteChildRange(start.getPsi(),end.getPsi());
                    }
                }));
    }
}
