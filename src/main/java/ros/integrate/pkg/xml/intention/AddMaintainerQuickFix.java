package ros.integrate.pkg.xml.intention;

import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ROSPackageXml;

/**
 * an intention that adds a maintainer tag to a package.xml
 * @author Noam Dori
 */
public class AddMaintainerQuickFix extends AddElementQuickFix {

    /**
     * construct a new intention
     * @param pkgXml the relevant package.xml file
     */
    public AddMaintainerQuickFix(ROSPackageXml pkgXml) {
        super(pkgXml);
    }

    @NotNull
    @Override
    public String getText() {
        return "Add maintainer";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return pkgXml.getMaintainers().isEmpty();
    }

    @Override
    public void doFix(@NotNull Editor editor) {
        pkgXml.addMaintainer("user","user@todo.todo");
        TextRange range = pkgXml.getMaintainerTextRanges().get(0);
        Caret caret = editor.getCaretModel().getCurrentCaret();
        caret.moveToOffset(range.getStartOffset());
        caret.moveCaretRelatively("user".length(), 0, true, false);
    }
}
