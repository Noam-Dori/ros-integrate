package ros.integrate.pkg.xml.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
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
 * an intention that repairs dependency tags, so they can be valid
 * @author Noam Dori
 */
public class FixDependencyQuickFix extends BaseIntentionAction implements LocalQuickFix {
    @NotNull
    private final ROSPackageXml pkgXml;
    private final int id;
    private final boolean strongFix;

    /**
     * construct a new intention
     * @param pkgXml the relevant package.xml file
     * @param id the index of the tag in the package.xml
     * @param strongFix whether the intention may override the version range attributes to ensure validity,
     *                  specifically in relation to the package this tag points to
     */
    public FixDependencyQuickFix(@NotNull ROSPackageXml pkgXml, int id, boolean strongFix) {
        this.pkgXml = pkgXml;
        this.id = id;
        this.strongFix = strongFix;
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
        doFix();
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        doFix();
    }

    private void doFix() {
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
        String depVersion = Optional.ofNullable(dep.getPackage().getPackageXml()).map(ROSPackageXml::getVersion)
                .map(ROSPackageXml.Version::getValue).orElse(null);
        String compatibilityVersion = Optional.ofNullable(dep.getPackage().getPackageXml())
                .map(ROSPackageXml::getVersion).map(ROSPackageXml.Version::getCompatibility).orElse(depVersion);
        VersionRange depRange = new VersionRange.Builder()
                .min(compatibilityVersion, false).max(depVersion, false).build();
        if (strongFix && depVersion != null && !depRange.isNotValid() &&
                newBuilder.build().intersect(depRange) == null) {
            VersionRange.Builder check = new VersionRange.Builder(newBuilder);
            check.min(depVersion, false);
            if (check.build().isNotValid()) {
                newBuilder.max(depVersion, false);
            } else {
                newBuilder.min(depVersion, false);
            }
        }
        pkgXml.setDependency(id, new ROSPackageXml.Dependency(dep.getType(), dep.getPackage(),
                newBuilder.build(), dep.getCondition()));
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getName() {
        return getText();
    }
}
