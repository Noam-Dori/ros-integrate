package ros.integrate.pkg.xml.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.VersionRange;

import java.util.Optional;

/**
 * an intention that removes information from dependency tags so they can be valid.
 * @author Noam Dori
 */
public class AmputateDependencyQuickFix extends BaseIntentionAction {
    @NotNull
    private final ROSPackageXml pkgXml;
    private final int id;

    /**
     * construct a new intention
     * @param pkgXml the relevant package.xml file
     * @param id the index of the tag in the package.xml
     */
    public AmputateDependencyQuickFix(@NotNull ROSPackageXml pkgXml, int id) {
        this.pkgXml = pkgXml;
        this.id = id;
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return "Remove version attributes";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS XML";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return pkgXml.getDependencies(null).size() > id;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        ROSPackageXml.Dependency dep = pkgXml.getDependencies(null).get(id);
        VersionRange range = dep.getVersionRange();
        VersionRange.Builder newBuilder = new VersionRange.Builder();
        if (Optional.ofNullable(range.getMin()).orElse("").matches(VersionRange.VERSION_REGEX)) {
            newBuilder.min(range.getMin(), range.isStrictMin());
        } else if (Optional.ofNullable(range.getMax()).orElse("").matches(VersionRange.VERSION_REGEX)) {
            newBuilder.max(range.getMax(), range.isStrictMax());
        }
        pkgXml.setDependency(id, new ROSPackageXml.Dependency(dep.getType(), dep.getPackage(),
                newBuilder.build(), dep.getCondition()));
    }
}
