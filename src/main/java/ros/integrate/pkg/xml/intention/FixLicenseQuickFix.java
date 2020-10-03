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

/**
 * an intention that repairs a license tag in the package.xml
 * @author Noam Dori
 */
public class FixLicenseQuickFix extends BaseIntentionAction {
    @NotNull
    private final ROSPackageXml pkgXml;
    private final int id;

    /**
     * construct a new intention
     * @param pkgXml the relevant package.xml file
     * @param id the index of the tag in the package.xml
     */
    @Contract(pure = true)
    public FixLicenseQuickFix(@NotNull ROSPackageXml pkgXml, int id) {
        this.id = id;
        this.pkgXml = pkgXml;
    }

    @NotNull
    @Override
    public String getText() {
        return "Fix license";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS XML";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return pkgXml.getLicences().size() > id;
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull Editor editor, PsiFile file) throws IncorrectOperationException {
        pkgXml.setLicense(id, new ROSPackageXml.License("TODO", pkgXml.getLicences().get(id).getFile()));
        TextRange range = pkgXml.getLicenceTextRanges().get(id);
        Caret caret = editor.getCaretModel().getCurrentCaret();
        caret.moveToOffset(range.getStartOffset());
        caret.moveCaretRelatively(range.getLength(), 0, true, false);
    }
}
