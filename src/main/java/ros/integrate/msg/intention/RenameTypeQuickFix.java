package ros.integrate.msg.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.ide.DataManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.rename.RenameHandler;
import com.intellij.refactoring.rename.RenameProcessor;
import com.intellij.refactoring.rename.inplace.MemberInplaceRenameHandler;
import com.intellij.testFramework.MockProblemDescriptor;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.msg.psi.ROSMsgType;

public class RenameTypeQuickFix implements LocalQuickFix {
    @Nullable
    private String forcedRename;
    @Nullable
    private Editor editor;

    public RenameTypeQuickFix(@Nullable String forcedRename) {
        this.forcedRename = forcedRename;
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
        // activate refactoring on provided PSI element
        PsiElement elementToRename = descriptor.getPsiElement();

        if (forcedRename == null) {
            MemberInplaceRenameHandler handler = findHandler();
            if (editor != null && handler != null) {
                handler.doRename(elementToRename,editor, DataManager.getInstance().getDataContext(editor.getComponent())); // context might need to not be null
            }
        } else {
            // TODO: the booleans in here should be toggleable via intention settings
            RenameProcessor processor = new RenameProcessor(project, elementToRename.getParent(), forcedRename,
                    false, false);
            processor.doRun();
        }
    }

    @Nullable
    private static MemberInplaceRenameHandler findHandler() {
        Object[] extensions = Extensions.getExtensions(RenameHandler.EP_NAME);

        for (Object extension : extensions) {
            if (extension instanceof MemberInplaceRenameHandler) {
                return (MemberInplaceRenameHandler)extension;
            }
        }
        return null;
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    public static class RenameTypeIntention extends BaseIntentionAction {
        @NotNull private final RenameTypeQuickFix fix = new RenameTypeQuickFix(null);
        @NotNull private ROSMsgType type;
        String message;

        public RenameTypeIntention(@NotNull ROSMsgType fieldType, String message) {
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
