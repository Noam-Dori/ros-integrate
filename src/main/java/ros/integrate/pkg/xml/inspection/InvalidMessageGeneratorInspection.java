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

public class InvalidMessageGeneratorInspection extends LocalInspectionTool {
    @NotNull
    private static final List<ExportLangHelper> HELPERS = ExportLangHelper.EP_NAME.getExtensionList();

    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        List<ProblemDescriptor> problem = new ArrayList<>();
        Optional.ofNullable(PackageXmlUtil.getWrapper(file))
                .map(ROSPackageXml::getExport).ifPresent(export -> {
            String lang = export.getMessageGenerator();
            if (lang == null || lang.isEmpty()) {
                return;
            }
            for (ExportLangHelper helper : HELPERS) {
                if (helper.messageGeneratorFor(lang, export.getParent().getPackage())) {
                    return;
                }
            }
            problem.add(manager.createProblemDescriptor(file, export.getMessageGeneratorTextRange(),
                    getDisplayName() + " " + lang, ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                    isOnTheFly));
        });
        return problem.size() == 0 ? null : problem.toArray(ProblemDescriptor.EMPTY_ARRAY);
    }
}
