package ros.integrate.pkg.xml.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ROSPackageXml;

public class FixContributorQuickFix extends BaseIntentionAction {

    @NotNull
    private final ROSPackageXml pkgXml;
    private final int id;
    @NotNull
    private final ContribType type;

    @Contract(pure = true)
    public FixContributorQuickFix(@NotNull ROSPackageXml pkgXml, int id, @NotNull ContribType type) {
        this.pkgXml = pkgXml;
        this.id = id;
        this.type = type;
    }

    @NotNull
    @Override
    public String getText() {
        return "Fix contributor";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS XML";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return id < (type == ContribType.AUTHOR ? pkgXml.getAuthors() : pkgXml.getMaintainers()).size();
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        if(type.fix(pkgXml, id)) {
            TextRange range = type.getTr(pkgXml,id);
            Caret caret = editor.getCaretModel().getCurrentCaret();
            caret.moveToOffset(range.getStartOffset());
            caret.moveCaretRelatively(range.getLength(), 0, true, false);
        }
    }
}
