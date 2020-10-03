package ros.integrate.pkg.ref;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.ROSPackageManager;
import ros.integrate.pkg.psi.ROSPackage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * a base class representing a reference to a ROS package ({@link ROSPackage}).
 * @param <T> the class of the PSI element that is referencing a ROS package.
 */
public abstract class ROSPackageReferenceBase<T extends PsiElement> extends PsiPolyVariantReferenceBase<T> {

    protected String pkgName;

    /**
     * construct a new reference
     * @param element the referencing element.
     * @param range Reference range relative to given element}.
     */
    protected ROSPackageReferenceBase(@NotNull T element, TextRange range) {
        super(element, range, true);
    }

    @Nullable
    @Override
    public ROSPackage resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length > 0 ? (ROSPackage) resolveResults[0].getElement() : null;
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        if(!(element instanceof ROSPackage)) {
            throw new IncorrectOperationException("Cannot bind to " + element);
        }
        return handleElementRename(((ROSPackage)element).getName());
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        List<ResolveResult> ret = new ArrayList<>();
        resolvePackage().ifPresent(pkg -> {
                    ret.add(new PsiElementResolveResult(pkg));
                    Arrays.stream(pkg.getRoots())
                            .map(PsiElementResolveResult::new)
                            .forEach(ret::add);
                });
        return ret.toArray(ResolveResult.EMPTY_ARRAY);
    }

    /**
     * the method that figures out what package the element is referencing.
     * @return the package this element is referencing. It can also fail and return {@link Optional#empty()}
     */
    @NotNull
    protected Optional<ROSPackage> resolvePackage() {
        return Optional.ofNullable(myElement.getProject().getService(ROSPackageManager.class).findPackage(pkgName));
    }
}
