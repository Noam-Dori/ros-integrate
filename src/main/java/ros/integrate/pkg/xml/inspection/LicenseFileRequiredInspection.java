package ros.integrate.pkg.xml.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.xml.*;
import ros.integrate.pkg.xml.ROSLicenses.LicenseEntity;
import ros.integrate.pkg.xml.ROSPackageXml.License;
import ros.integrate.pkg.xml.intention.AddLicenseFileFix;

import java.util.ArrayList;
import java.util.List;

public class LicenseFileRequiredInspection extends LocalInspectionTool {
    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        ROSPackageXml pkgXml = PackageXmlUtil.getWrapper(file);
        if (pkgXml == null || pkgXml.getFormat() < 3) {
            return null;
        }
        List<License> licenses = pkgXml.getLicences();
        List<TagTextRange> licenseTrs = pkgXml.getLicenceTextRanges();
        List<ProblemDescriptor> ret = new ArrayList<>();
        for (int i = 0; i < licenses.size(); i++) {
            License license = licenses.get(i);
            LicenseEntity entity = ROSLicenses.AVAILABLE_LICENSES.get(license.getValue());
            if (entity != null && entity.isFileRequired() && license.getFile() == null
                    && !entity.getFileSource().isEmpty()) {
                ret.add(manager.createProblemDescriptor(file, licenseTrs.get(i).value(), getDisplayName(),
                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly,
                        new AddLicenseFileFix(pkgXml, i, entity.getFileSource())));
            }
        }
        return ret.toArray(ProblemDescriptor.EMPTY_ARRAY);
    }
}
