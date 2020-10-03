package ros.integrate.pkg.xml.intention;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.ROSDepKeyCache;
import ros.integrate.pkg.xml.PackageXmlUtil;

/**
 * an intention that forces the plugin to download and cache all dependency keys from the internet
 * @author Noam Dori
 */
public class ForceCacheQuickFix extends BaseIntentionAction implements LocalQuickFix {

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS XML";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getName() {
        return "Try to fetch keys from dependency lists";
    }

    @NotNull
    @Override
    public String getText() {
        return getName();
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        project.getService(ROSDepKeyCache.class).forceFetch();
        DaemonCodeAnalyzer.getInstance(project).restart();
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return PackageXmlUtil.getWrapper(file) != null;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        project.getService(ROSDepKeyCache.class).forceFetch();
        DaemonCodeAnalyzer.getInstance(project).restart();
    }
}
