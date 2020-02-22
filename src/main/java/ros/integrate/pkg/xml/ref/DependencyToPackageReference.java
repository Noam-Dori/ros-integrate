package ros.integrate.pkg.xml.ref;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.pkg.ref.ROSPackageReferenceBase;

/**
 * a class defining the references of {@link ros.integrate.pkg.xml.ROSPackageXml} to {@link ROSPackage} and its affiliated roots.
 */
public class DependencyToPackageReference extends ROSPackageReferenceBase<XmlTag> {
    // note: myElement is the referencing element, and the result of resolve() is the original element (the file).

    public DependencyToPackageReference(@NotNull XmlTag element) {
        super(element, getTextRange(element));
        pkgName = element.getValue().getText();
    }

    @NotNull
    private static TextRange getTextRange(@NotNull XmlTag element) {
        return element.getValue().getTextRange().shiftLeft(element.getTextOffset());
    }
}
