package ros.integrate.pkg.xml.intention;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ExportTag;

/**
 * an intention that removes the condition attribute from a build_type tag
 * @author Noam Dori
 */
public class RemoveBuildTypeConditionFix implements LocalQuickFix {
    private final int id;
    @NotNull
    private final ExportTag export;

    /**
     * construct a new intention
     * @param export the relevant export tag
     * @param id the index of the tag in the export tag
     */
    public RemoveBuildTypeConditionFix(@NotNull ExportTag export, int id) {
        this.id = id;
        this.export = export;
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getName() {
        return "Remove condition";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS XML";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        ExportTag.BuildType old = export.getBuildTypes().get(id);
        export.setBuildType(id, new ExportTag.BuildType(old.getType(), null));
    }

}
