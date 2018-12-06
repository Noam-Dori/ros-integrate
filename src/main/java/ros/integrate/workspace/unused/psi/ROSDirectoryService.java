package ros.integrate.workspace.unused.psi;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.Nullable;
import ros.integrate.workspace.psi.ROSPackage;

public abstract class ROSDirectoryService {
    public static ROSDirectoryService getInstance() {
        return ServiceManager.getService(ROSDirectoryService.class);
    }

    /**
     * Returns the package corresponding to the directory.
     *
     * @return the package instance, or null if the directory does not correspond to any package.
     */
    @Nullable
    public abstract ROSPackage getPackage(PsiDirectory directory);
}
