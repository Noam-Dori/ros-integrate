package ros.integrate.pkg.xml.intention;

import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ROSPackageXml;

/**
 * an intention that adds a license tag to a package.xml
 * @author Noam Dori
 */
public class AddLicenseQuickFix extends AddElementQuickFix {

    /**
     * construct a new intention
     * @param pkgXml the relevant package.xml file
     */
    public AddLicenseQuickFix(ROSPackageXml pkgXml) {
        super(pkgXml);
    }

    @NotNull
    @Override
    public String getText() {
        return "Add licence";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return pkgXml.getLicences().isEmpty();
    }

    void doFix(@NotNull Editor editor) {
        pkgXml.addLicence("TODO", null);
        TextRange range = pkgXml.getLicenceTextRanges().get(0);
        Caret caret = editor.getCaretModel().getCurrentCaret();
        caret.moveToOffset(range.getStartOffset());
        caret.moveCaretRelatively("TODO".length(), 0, true, false);
    }
}
