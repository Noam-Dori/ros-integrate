package ros.integrate.pkg.xml.intention;

import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ROSPackageXml;

/**
 * an intention that adds a description tag to a package.xml
 * @author Noam Dori
 */
public class AddDescriptionQuickFix extends AddElementQuickFix {

    /**
     * construct a new intention
     * @param pkgXml the relevant package.xml file
     */
    @Contract(pure = true)
    public AddDescriptionQuickFix(ROSPackageXml pkgXml) {
        super(pkgXml);
    }

    @NotNull
    @Override
    public String getText() {
        return "Add description";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return pkgXml.getDescription() == null;
    }

    void doFix(@NotNull Editor editor) {
        pkgXml.setDescription("\nPackage description here\n");
        TextRange range = pkgXml.getDescriptionTextRange();
        Caret caret = editor.getCaretModel().getCurrentCaret();
        caret.moveToOffset(range.getStartOffset() + 1);
        caret.moveCaretRelatively("Package description here".length(), 0, true, false);
    }
}
