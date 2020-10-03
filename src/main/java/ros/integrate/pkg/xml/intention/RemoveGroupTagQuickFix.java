package ros.integrate.pkg.xml.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ROSPackageXml;

/**
 * an intention that removes a group tag (group_depend or member_of_group) from the package.xml
 * @author Noam Dori
 */
public class RemoveGroupTagQuickFix extends BaseIntentionAction implements LocalQuickFix {

    @NotNull
    private final ROSPackageXml pkgXml;
    private final int id;
    private final boolean isDependency;

    /**
     * construct a new intention
     * @param pkgXml the relevant package.xml file
     * @param id the index of the tag in the package.xml
     * @param isDependency true if the tag is a group_depend tag, false if it is a member_of_group tag
     */
    @Contract(pure = true)
    public RemoveGroupTagQuickFix(@NotNull ROSPackageXml pkgXml, int id, boolean isDependency) {
        this.id = id;
        this.pkgXml = pkgXml;
        this.isDependency = isDependency;
    }

    @NotNull
    @Override
    public String getText() {
        return "Remove group tag";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS XML";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        if (isDependency) {
            pkgXml.removeGroupDependency(id);
        } else {
            pkgXml.removeGroup(id);
        }
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return (isDependency ? pkgXml.getGroupDepends().size() : pkgXml.getGroups().size()) > id;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        if (isDependency) {
            pkgXml.removeGroupDependency(id);
        } else {
            pkgXml.removeGroup(id);
        }
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getName() {
        return getText();
    }
}
