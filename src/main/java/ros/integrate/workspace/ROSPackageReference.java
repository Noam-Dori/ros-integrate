package ros.integrate.workspace;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.PsiFileReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.psi.ROSPktTypeBase;
import ros.integrate.workspace.psi.ROSPackage;

/**
 * a class defining the references of {@link ROSPktTypeBase} to {@link ROSPackage}
 */
public class ROSPackageReference extends PsiReferenceBase<PsiElement> implements PsiFileReference {
    // note: myElement is the referencing element, and the result of resolve() is the original element (the file).

    @NotNull
    private String pkgName;

    ROSPackageReference(@NotNull ROSPktTypeBase element, @NotNull TextRange textRange) {
        super(element, textRange);
        pkgName = element.raw().getText().replaceAll("/.*","");
        assert pkgName.length() == textRange.getLength();
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Project project = myElement.getProject();
        ROSPackage pkg = project.getComponent(ROSPackageManager.class).findPackage(pkgName);
        if (pkg == null) {
            return ResolveResult.EMPTY_ARRAY;
        }
        return new ResolveResult[]{new PsiElementResolveResult(pkg)};
    }

    @Nullable
    @Override
    public ROSPackage resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? (ROSPackage) resolveResults[0].getElement() : null;
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        String pkt = myElement.getText().replaceAll(".*/","");
        return super.handleElementRename(newElementName.equals(pkgName) ? "" : (newElementName + "/") + pkt);
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        if(!(element instanceof ROSPackage)) {
            throw new IncorrectOperationException("Cannot bind to " + element);
        }
        return handleElementRename(((ROSPackage)element).getName());
    }
}
