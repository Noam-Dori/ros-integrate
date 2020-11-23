package ros.integrate.pkg.xml.inspection;

import com.intellij.codeInspection.*;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.xml.DependencyType;
import ros.integrate.pkg.xml.PackageXmlUtil;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.intention.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PackageXmlCompletionInspection extends LocalInspectionTool {
    public ResolutionStrategy selectedStrategy = ResolutionStrategy.AUTO_FIX;

    private enum ResolutionStrategy {
        AUTO_FIX,
        DIALOG,
        MANUAL_FIX;

        @NotNull
        @Override
        public String toString() {
            return super.toString().toLowerCase().replace("_","-");
        }
    }

    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        if (!(file instanceof XmlFile)) {
            return null;
        }

        ROSPackageXml pkgXml = PackageXmlUtil.getWrapper(file);
        if (pkgXml == null) {
            return null;
        }

        List<ProblemDescriptor> ret = new ArrayList<>();
        runCheck(pkgXml.getPkgName() == null, "Package must give a name to the package",
                new FixNameQuickFix(pkgXml, "Add"), manager, pkgXml, isOnTheFly, ret);
        runCheck(pkgXml.getVersion() == null, "Package must have a version.",
                new FixVersionQuickFix(pkgXml, "Add"), manager, pkgXml, isOnTheFly, ret);
        runCheck(pkgXml.getDescription() == null, "Package must have a description.",
                new AddDescriptionQuickFix(pkgXml), manager, pkgXml, isOnTheFly, ret);
        runCheck(pkgXml.getLicences().isEmpty(), "Package must have at least one licence.",
                new AddLicenseQuickFix(pkgXml), manager, pkgXml, isOnTheFly, ret);
        runCheck(pkgXml.getMaintainers().isEmpty(), "Package must have a description.",
                new AddMaintainerQuickFix(pkgXml), manager, pkgXml, isOnTheFly, ret);
        runCheck(pkgXml.getDependencies(DependencyType.BUILDTOOL).isEmpty(), "Package must have a description.",
                new AddBuildtoolDependencyFix(pkgXml), manager, pkgXml, isOnTheFly, ret);

        return ret.toArray(ProblemDescriptor.EMPTY_ARRAY);
    }

    private void runCheck(boolean success, String message, LocalQuickFix manualFix,
                          InspectionManager manager, @NotNull ROSPackageXml pkgXml, boolean isOnTheFly,
                          List<ProblemDescriptor> ret) {
        if (success) {
            if (selectedStrategy == ResolutionStrategy.MANUAL_FIX) {
                ret.add(manager.createProblemDescriptor(pkgXml.getRawXml(), pkgXml.getRootTextRange(),
                        message, ProblemHighlightType.GENERIC_ERROR, isOnTheFly, manualFix));
            } else if (ret.isEmpty()) {
                ret.add(manager.createProblemDescriptor(pkgXml.getRawXml(), pkgXml.getRootTextRange(),
                        "Package XML is incomplete", ProblemHighlightType.GENERIC_ERROR, isOnTheFly,
                        new CompletePackageXmlFix(pkgXml, selectedStrategy == ResolutionStrategy.DIALOG)));
            }
        }
    }

    @Nullable
    @Override
    public JComponent createOptionsPanel() {
        ComboBox<ResolutionStrategy> strategyOptions = new ComboBox<>(ResolutionStrategy.values());
        strategyOptions.setItem(selectedStrategy);
        strategyOptions.addItemListener(event -> selectedStrategy = strategyOptions.getItem());

        JPanel unalignedPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent("Error and resolution strategy", strategyOptions)
                .getPanel();
        JPanel ret = new JPanel(new BorderLayout());
        ret.add(unalignedPanel, BorderLayout.NORTH);
        return ret;
    }
}
