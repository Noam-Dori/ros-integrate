package ros.integrate.pkg.xml.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ExportTag;

/**
 * an intention that removes a build_type tag from the export tag
 * @author Noam Dori
 */
public class RemoveBuildTypeQuickFix extends BaseIntentionAction {
    @NotNull
    private final ExportTag export;
    private final int id;

    /**
     * construct a new intention
     * @param export the relevant export tag
     * @param id the index of the tag in the export tag
     */
    @Contract(pure = true)
    public RemoveBuildTypeQuickFix(@NotNull ExportTag export, int id) {
        this.id = id;
        this.export = export;
    }

    @NotNull
    @Override
    public String getText() {
        return "Remove dependency";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS XML";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return export.getBuildTypes().size() > id;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) {
        export.removeBuildType(id);
    }
}
