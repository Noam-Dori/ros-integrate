package ros.integrate.pkg.xml.condition.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.condition.psi.ROSConditionElementFactory;

public class CleanItemQuickFix extends BaseIntentionAction {
    @NotNull
    private final PsiElement element;

    public CleanItemQuickFix(@NotNull PsiElement element) {
        this.element = element;
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS condition";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return "Clean variable/literal";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) {
        String newText = getNewText();
        if (newText.isEmpty()) {
            element.delete();
        } else {
            element.replace(ROSConditionElementFactory.createItem(project, newText));
        }
    }

    @NotNull
    private String getNewText() {
        String oldText = element.getText();
        if (oldText.startsWith("$")) {
            return "$" + oldText.replaceAll("[^a-zA-Z0-9_]", "");
        } else {
            return oldText.replaceAll("[^-a-zA-Z0-9_]", "");
        }
    }
}
