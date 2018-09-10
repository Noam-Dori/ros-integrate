package ros.integrate.msg.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.MockProblemDescriptor;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.msg.psi.ROSMsgType;

public class RenameTypeQuickFix implements LocalQuickFix {

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS Message/Service inspections";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        // activate refactoring on provided PSI element
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    public static class RenameTypeIntention extends BaseIntentionAction {
        @NotNull private final RenameTypeQuickFix fix = new RenameTypeQuickFix();
        @NotNull private ROSMsgType type;

        public RenameTypeIntention(@NotNull ROSMsgType fieldType) {
            type = fieldType;
        }

        @Nls(capitalization = Nls.Capitalization.Sentence)
        @NotNull
        @Override
        public String getText() {
            return "Rename message type";
        }

        @NotNull
        @Override
        public String getFamilyName() {
            return fix.getFamilyName();
        }

        public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
            return true;
        }

        @Override
        public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
            fix.applyFix(project, new MockProblemDescriptor(type, "some template",ProblemHighlightType.ERROR, fix));
        }
    }
}
