package ros.integrate.pkg;

import com.intellij.ide.IconProvider;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.psi.ROSPackage;

import javax.swing.*;

/**
 * adds the package icon to the root package directories, mainly in the project view but in other places too
 * @author Noam Dori
 */
public class ROSPackageIconProvider extends IconProvider {
    @Nullable
    @Override
    public Icon getIcon(@NotNull PsiElement element, int flags) {
        if (!(element instanceof PsiDirectory dir)) {
            return null;
        }
        var pkgManager = ROSPackageManager.getInstance(element.getProject());
        if (!pkgManager.isLoaded()) {
            return null;
        }
        ROSPackage pkg = pkgManager.findPackage(dir);
        return pkg != null &&
                search(pkg.getRoots(), dir)
                ? pkg.getIcon(flags) : null;
    }

    @Contract(pure = true)
    private boolean search(@NotNull PsiDirectory @NotNull [] roots, PsiDirectory potentialRoot) {
        for (PsiDirectory root : roots) {
            if(root.getVirtualFile().getPath().equals(potentialRoot.getVirtualFile().getPath())) {
                return true;
            }
        }
        return false;
    }
}
