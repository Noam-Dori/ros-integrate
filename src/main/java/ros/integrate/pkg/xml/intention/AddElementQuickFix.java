package ros.integrate.pkg.xml.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ROSPackageXml;

/**
 * An abstract template for intentions that add new elements to a package.xml file.
 * @author Noam Dori
 */
public abstract class AddElementQuickFix extends BaseIntentionAction implements LocalQuickFix {
    @NotNull
    protected final ROSPackageXml pkgXml;

    /**
     * construct a new intention
     * @param pkgXml the relevant package.xml file
     */
    public AddElementQuickFix(@NotNull ROSPackageXml pkgXml) {
        this.pkgXml = pkgXml;
    }

    /**
     * Called when user invokes intention. This method is called inside command.
     * If {@link #startInWriteAction()} returns {@code true}, this method is also called
     * inside write action.
     *
     * @param editor  the editor in which the intention is invoked.
     */
    abstract void doFix(@NotNull Editor editor);

    @Override
    public void invoke(@NotNull Project project, @NotNull Editor editor, PsiFile file) throws IncorrectOperationException {
        doFix(editor);
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS XML";
    }

    @NotNull
    @Override
    public @IntentionName String getName() {
        return getText();
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor != null) {
            doFix(editor);
        }
    }
}
