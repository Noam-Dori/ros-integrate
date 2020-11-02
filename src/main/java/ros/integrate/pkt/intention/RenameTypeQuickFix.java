package ros.integrate.pkt.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.actions.RenameElementAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

/**
 * an intention used to refactor the name of a message (or service or action) type
 * @author Noam Dori
 */
public class RenameTypeQuickFix extends BaseIntentionAction implements LocalQuickFix {
    @Nullable
    private final FileEditor fileEditor;
    @NotNull
    private final String msgComponent;

    /**
     * construct a new intention
     *
     * @param editor the editor of the file this intention applies to
     * @param msgComponent the component's name to be edited. Used in the message.
     */
    public RenameTypeQuickFix(@Nullable FileEditor editor, @NotNull String msgComponent) {
        this.fileEditor = editor;
        this.msgComponent = msgComponent;
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return fileEditor == null ? "ROS message/service" : getText();
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        doFix(Objects.requireNonNull(fileEditor).getComponent());
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    @NotNull
    @Override
    public @IntentionName String getText() {
        return "Rename field " + msgComponent;
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull Editor editor, PsiFile file) {
        doFix(editor.getComponent());
    }

    public void doFix(@NotNull JComponent component) {
        new RenameElementAction().actionPerformed(new AnActionEvent(
                null, DataManager.getInstance().getDataContext(component),
                ActionPlaces.UNKNOWN, new Presentation(), ActionManager.getInstance(), 0));
    }
}
