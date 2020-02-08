package ros.integrate.pkg.xml.ref;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.pkg.ref.ROSPackageReferenceBase;

/**
 * a class defining the references of {@link ros.integrate.pkg.xml.ROSPackageXml} to {@link ROSPackage} and its affiliated roots.
 */
public class PkgXmlToPackageReference extends ROSPackageReferenceBase<XmlTag> {
    // note: myElement is the referencing element, and the result of resolve() is the original element (the file).

    public PkgXmlToPackageReference(@NotNull XmlTag element) {
        super(element, getTextRange(element));
        pkgName = element.getValue().getText();
    }

    @NotNull
    private static TextRange getTextRange(@NotNull XmlTag element) {
        return element.getValue().getTextRange().shiftLeft(element.getTextOffset());
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        return super.handleElementRename(myElement.getText().replaceFirst(">.*<", ">" + newElementName + "<"));
    }
}
