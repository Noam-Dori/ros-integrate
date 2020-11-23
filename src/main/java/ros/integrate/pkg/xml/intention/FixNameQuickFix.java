package ros.integrate.pkg.xml.intention;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ROSPackageXml;

/**
 * an intention that repairs a name tag in the package.xml (or adds it)
 * @author Noam Dori
 */
public class FixNameQuickFix extends AddElementQuickFix {

    /**
     * construct a new intention
     * @param pkgXml the relevant package.xml file
     * @param prefix the intention description prefix
     */
    @Contract(pure = true)
    public FixNameQuickFix(ROSPackageXml pkgXml, String prefix) {
        super(pkgXml);
        setText(prefix + " package name");
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return !pkgXml.getPackage().getName().equals(pkgXml.getPkgName());
    }

    @Override
    public void doFix(@NotNull Editor editor) {
        pkgXml.setPkgName(pkgXml.getPackage().getName());
    }
}
