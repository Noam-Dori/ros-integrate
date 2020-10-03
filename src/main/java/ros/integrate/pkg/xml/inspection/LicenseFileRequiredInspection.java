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

/**
 * <p>Most licenses, open source or not, require the full license text to be included in its own file in the package. This
 *     file should be referenced from the "file" attribute.
 *     This inspection checks whether or not the license requires a file and raises a warning if no linked file is found.
 * </p>
 * <p>for example, consider this license tag:</p>
 * <code>&lt;license&gt;Apache-2.0&lt;/license&gt;</code>
 * <p>The apache license is infamous for how it requires the full license file to be part of your repository.
 *     Thus, this license tag should like to that file using the "file" attribute. Since this specific tag does not do this,
 *     it is annotated.
 * </p>
 * <p>this inspection offers one fix:</p>
 * <ol>
 *     <li>Add the respective license file and link it. This will also bring up a dialogue where you can fill in information
 *         so the license is specific for your company/product.</li>
 * </ol>
 * <p>Notable licenses that do not require full texts are:</p>
 * <ul>
 *     <li>0BSD</li>
 *     <li>ZLib</li>
 *     <li>CC0</li>
 *     <li>Public Domain equivalent licenses</li>
 * </ul>
 * @author Noam Dori
 */
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
