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
import ros.integrate.pkg.xml.intention.ReformatPackageXmlFix;

/**
 * <p>Checks if a manifest uses the latest format available.</p>
 * <p>the "format" attribute is a property of the file itself, present in the <code>package</code> tag. It is 1 by default,
 *     but the standard format version ROS uses is now higher.
 * </p>
 * <p>this inspection offers one "fix":</p>
 * <ol>
 *     <li>Update the package to use the latest format. This will also reformat the package and order its contents
 *         correctly</li>
 * </ol>
 * <p>This is not a serious issue. You can leave packages in the old formats but you lose out on a lot of
 *     features</p>
 * @author Noam Dori
 */
public class OutdatedFormatInspection extends LocalInspectionTool {
    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        ROSPackageXml pkgXml = PackageXmlUtil.getWrapper(file);
        if (pkgXml == null || pkgXml.getFormat() >= ROSPackageXml.getLatestFormat() ||
                pkgXml.getFormatTextRange().isEmpty()) { // this aborts the inspection since there is already an error.
            return null;
        }
        return new ProblemDescriptor[]{
                manager.createProblemDescriptor(file, pkgXml.getFormatTextRange(), getDisplayName(),
                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly,
                        new ReformatPackageXmlFix(pkgXml, true))
        };
    }
}
