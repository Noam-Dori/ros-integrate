package ros.integrate.pkg.ref;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.psi.ROSPktTypeBase;
import ros.integrate.pkg.psi.ROSPackage;

/**
 * a class defining the references from {@link ROSPktTypeBase} to {@link ROSPackage} and its affiliated roots.
 */
class ROSPktToPackageReference extends ROSPackageReferenceBase<ROSPktTypeBase> {
    // note: myElement is the referencing element, and the result of resolve() is the original element (the file).

    /**
     * construct a new reference
     * @param element the referencing element.
     * @param textRange Reference range relative to given element}.
     */
    ROSPktToPackageReference(@NotNull ROSPktTypeBase element, @NotNull TextRange textRange) {
        super(element, textRange);
        pkgName = element.raw().getText().replaceAll("/.*","");
        assert pkgName.length() == textRange.getLength();
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        String pkt = myElement.getText().replaceAll(".*/","");
        return super.handleElementRename((newElementName.equals(pkgName) ? "" : (newElementName + "/")) + pkt);
    }
}
