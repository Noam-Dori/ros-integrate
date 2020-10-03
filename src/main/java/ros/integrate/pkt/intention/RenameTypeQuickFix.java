package ros.integrate.pkt.intention;

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
import ros.integrate.pkt.psi.ROSPktTypeBase;

import javax.swing.*;
import java.util.Objects;

/**
 * an intention used to refactor the name of a message (or service or action) type
 * @author Noam Dori
 */
public class RenameTypeQuickFix implements LocalQuickFix {
    @Nullable
    private Editor editor; // compliment of fileEditor, available only in annotation fixes.
    @Nullable
    private final FileEditor fileEditor; // compliment of editor, available only in inspection fixes.

    /**
     * construct a new intention
     * @param editor the editor of the file this intention applies to
     */
    public RenameTypeQuickFix(@Nullable FileEditor editor) {
        this.fileEditor = editor;
    }

    private void setEditor(@Nullable Editor editor) {
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

    /**
     * an intention action adapter for the "refactor" quick fix
     * TODO merge this class with the main one
     */
    public static class RenameTypeIntention extends BaseIntentionAction {
        @NotNull private final RenameTypeQuickFix fix = new RenameTypeQuickFix(null);
        @NotNull private final ROSPktTypeBase type;
        final String message;

        /**
         * construct a new intention
         * @param fieldType the field type to be refactored
         * @param message a message to be displayed to the user
         */
        public RenameTypeIntention(@NotNull ROSPktTypeBase fieldType, String message) {
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
