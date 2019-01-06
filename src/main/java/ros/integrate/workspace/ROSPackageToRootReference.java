package ros.integrate.workspace;

import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.workspace.psi.ROSPackage;

/**
 * a class defining the references of {@link ROSPackage} to {@link PsiDirectory}
 */
public class ROSPackageToRootReference extends PsiReferenceBase<PsiElement> {
    // note: myElement is the referencing element, and the result of resolve() is the original element (the file).

    @NotNull
    private PsiDirectory target;

    ROSPackageToRootReference(@NotNull ROSPackage element, @NotNull PsiDirectory root) {
        super(element);
        target = root;
    }

    @Nullable
    @Override
    public PsiDirectory resolve() {
        return target;
    }
}
