package ros.integrate.pkg.xml.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.xml.PackageXmlUtil;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.annotate.ExportLangHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <p>If a package is annotated as architecture independent, this inspection makes sure that the compilation of the package
 *     does not depend on the architecture of the system, like on the CPU/GPU used, etc.
 * </p>
 * <p>python packages are considered architecture independent, while C/C++ packages depend on the architecture.</p>
 * <p>Please note that this inspection is not exhaustive and cannot account for every language.</p>
 * <p>Support for additional languages can be added via plugins.</p>
 * @author Noam Dori
 */
public class NotArchitectureIndependentInspection extends LocalInspectionTool {
    @NotNull
    private static final List<ExportLangHelper> HELPERS = ExportLangHelper.EP_NAME.getExtensionList();

    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        List<ProblemDescriptor> problem = new ArrayList<>();
        Optional.ofNullable(PackageXmlUtil.getWrapper(file))
                .map(ROSPackageXml::getExport).ifPresent(export -> {
            if (!export.markedArchitectureIndependent()) {
                return;
            }
            for (ExportLangHelper helper : HELPERS) {
                if (helper.dependsOnArchitecture(export.getParent().getPackage())) {
                    problem.add(manager.createProblemDescriptor(file, export.getArchitectureIndependentTextRange().name(),
                            getDisplayName(), ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly));
                    break;
                }
            }
        });
        return problem.size() == 0 ? null : problem.toArray(ProblemDescriptor.EMPTY_ARRAY);
    }
}
