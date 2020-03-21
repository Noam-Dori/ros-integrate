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

public class FixDependencyQuickFix extends BaseIntentionAction {
    @NotNull
    private final ROSPackageXml pkgXml;
    private final int id;

    public FixDependencyQuickFix(@NotNull ROSPackageXml pkgXml, int id) {
        this.pkgXml = pkgXml;
        this.id = id;
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return "Fix dependency";
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
        String newMin = null, newMax = null;
        if (range.getMax() != null) {
            newMax = VersionRepairUtil.repairVersion(range.getMax());
            newBuilder.max(newMax, range.isStrictMax());
        }
        if (range.getMin() != null) {
            newMin = VersionRepairUtil.repairVersion(range.getMin());
            newBuilder.min(newMin, range.isStrictMin());
        }
        if (newMax != null && newMin != null && newBuilder.build().isNotValid()) {
            if (newMax.equals(newMin)) {
                newBuilder.exactVersion(newMax);
            } else {
                newBuilder.max(newMin, range.isStrictMin());
                newBuilder.min(newMax, range.isStrictMax());
            }
        }
        pkgXml.setDependency(id, new ROSPackageXml.Dependency(dep.getType(), dep.getPackage(), newBuilder.build()));
    }
}
