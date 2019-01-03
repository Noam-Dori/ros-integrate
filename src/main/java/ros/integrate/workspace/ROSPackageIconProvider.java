package ros.integrate.workspace;

import com.intellij.ide.IconProvider;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.workspace.psi.ROSPackage;

import javax.swing.*;

public class ROSPackageIconProvider extends IconProvider {
    @Nullable
    @Override
    public Icon getIcon(@NotNull PsiElement element, int flags) {
        if(element instanceof PsiDirectory) {
            ROSPackage pkg = element.getProject().getComponent(ROSPackageManager.class).findPackage((PsiDirectory) element);
            return pkg != null &&
                    search(pkg.getRoots(), (PsiDirectory) element)
                    ? pkg.getIcon(flags) : null;
        }
        return null;
    }

    @Contract(pure = true)
    private boolean search(@NotNull PsiDirectory[] roots, PsiDirectory potentialRoot) {
        for (PsiDirectory root : roots) {
            if(root == potentialRoot) {
                return true;
            }
        }
        return false;
    }
}
