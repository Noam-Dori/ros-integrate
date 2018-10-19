package ros.integrate.msg.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
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
import com.intellij.testFramework.MockProblemDescriptor;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.msg.psi.ROSPktType;

import javax.swing.*;
import java.util.Objects;

/**
 * a fix used to trigger refactoring for a certain type.
 */
public class RenameTypeQuickFix implements LocalQuickFix {
    @Nullable
    private Editor editor; // compliment of fileEditor, available only in annotation fixes.
    @Nullable
    private FileEditor fileEditor; // compliment of editor, available only in inspection fixes.

    public RenameTypeQuickFix(@Nullable FileEditor editor) {
        this.fileEditor = editor;
    }

    public void setEditor(@Nullable Editor editor) {
        this.editor = editor;
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "Rename message type";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        JComponent component = editor == null ? Objects.requireNonNull(fileEditor).getComponent() : editor.getComponent();
        new RenameElementAction().actionPerformed(new AnActionEvent(
                null, DataManager.getInstance().getDataContext(component),
                ActionPlaces.UNKNOWN, new Presentation(), ActionManager.getInstance(), 0));
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    public static class RenameTypeIntention extends BaseIntentionAction {
        @NotNull private final RenameTypeQuickFix fix = new RenameTypeQuickFix(null);
        @NotNull private ROSPktType type;
        String message;

        public RenameTypeIntention(@NotNull ROSPktType fieldType, String message) {
            type = fieldType;
            this.message = message;
        }

        @Nls(capitalization = Nls.Capitalization.Sentence)
        @NotNull
        @Override
        public String getText() {
            return fix.getFamilyName();
        }

        @Nls(capitalization = Nls.Capitalization.Sentence)
        @NotNull
        @Override
        public String getFamilyName() {
            return "ROS Message/Service inspections";
        }

        public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
            return true;
        }

        @Override
        public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
            fix.setEditor(editor);
            fix.applyFix(project, new MockProblemDescriptor(type, message,ProblemHighlightType.ERROR, fix));
        }
    }
}
