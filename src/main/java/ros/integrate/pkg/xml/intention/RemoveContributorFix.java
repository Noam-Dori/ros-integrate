package ros.integrate.pkg.xml.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ROSPackageXml;

/**
 * an intention that removes author or maintainer tags from a package.xml file
 * @author Noam Dori
 */
public class RemoveContributorFix extends BaseIntentionAction {

    @NotNull
    private final ROSPackageXml pkgXml;
    private final int id;
    @NotNull
    private final ContribType type;

    /**
     * construct a new intention
     * @param pkgXml the relevant package.xml file
     * @param id the index of the tag in the package.xml
     * @param type the type of tag being repaired. This can be either author or maintainer
     */
    @Contract(pure = true)
    public RemoveContributorFix(@NotNull ROSPackageXml pkgXml, int id, @NotNull ContribType type) {
        this.pkgXml = pkgXml;
        this.id = id;
        this.type = type;
    }

    @NotNull
    @Override
    public String getText() {
        return "Remove contributor";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS XML";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        type.remove(pkgXml, id);
    }
}
