package ros.integrate.pkg.xml.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
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
 * <p>Allows this file to be used for indexing by the ROS plugin.
 *     Like its complement, XML exclusion can be redone from the ROS settings page and from the complement intention action.
 * </p>
 * <p>this means that even if this file does not belong to ros, the plugin will consider it as such</p>
 * @author Noam Dori
 */
public class UndoExcludeXml extends BaseIntentionAction {
    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS XML";
    }

    @NotNull
    @Override
    public String getText() {
        return "Undo ROS exclusion";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return file instanceof XmlFile && PackageXmlUtil.getWrapper(file) == null;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        project.getService(ROSPackageManager.class).includeXml((XmlFile) file);
    }
}
