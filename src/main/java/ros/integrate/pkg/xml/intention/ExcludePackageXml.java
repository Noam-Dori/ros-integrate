package ros.integrate.pkg.xml.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.ROSPackageManager;
import ros.integrate.pkg.xml.PackageXmlUtil;

/**
 * <p>Excludes an XML file from indexing by the ROS plugin.
 *     This action can be undone from the ROS Settings page and by the undo intention
 * </p>
 * <p>this means that even if this file is named "package.xml" it cannot be used as the manifest of a ROS package.
 *     Usually this means ROS is not used or the parent directory is not related to ROS
 * </p>
 * @author Noam Dori
 */
public class ExcludePackageXml extends BaseIntentionAction implements LocalQuickFix {
    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS XML";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        project.getService(ROSPackageManager.class).excludePkgXml((XmlFile) descriptor.getPsiElement()
                .getContainingFile());
    }

    @NotNull
    @Override
    public String getText() {
        return "Exclude file from ROS";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return file instanceof XmlFile && PackageXmlUtil.getWrapper(file) != null;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        project.getService(ROSPackageManager.class).excludePkgXml((XmlFile) file);
    }
}
