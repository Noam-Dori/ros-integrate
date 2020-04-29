package ros.integrate.pkg.xml.condition.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.condition.psi.ROSConditionElementFactory;
import ros.integrate.pkg.xml.condition.psi.ROSConditionExpr;
import ros.integrate.pkg.xml.condition.psi.ROSConditionToken;

public class PrependLogicQuickFix extends BaseIntentionAction {

    @NotNull
    private final ROSConditionToken token;
    @NotNull
    private final ROSConditionExpr expr;

    public PrependLogicQuickFix(@NotNull ROSConditionExpr expr, @NotNull ROSConditionToken token) {
        this.token = token;
        this.expr = expr;
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
        return "Prepend an operator before this token";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return token.getParent().equals(expr);
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull Editor editor, PsiFile file) {
        String textToAdd = "==";
        ROSConditionToken newPsi = ROSConditionElementFactory.createLogic(project, textToAdd);
        expr.addBefore(token.getPrevSibling().copy(), token.getPrevSibling()); // whitespace
        expr.addBefore(newPsi, token.getPrevSibling());
        Caret c = editor.getCaretModel().getCurrentCaret();
        c.moveToOffset(token.getPrevSibling().getPrevSibling().getTextOffset());
        c.moveCaretRelatively(textToAdd.length(), 0, true, false);
    }
}
