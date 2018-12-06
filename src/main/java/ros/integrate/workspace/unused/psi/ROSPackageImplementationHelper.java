package ros.integrate.workspace.unused.psi;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.search.GlobalSearchScope;
import ros.integrate.workspace.psi.ROSPackage;

public abstract class ROSPackageImplementationHelper {
    public static ROSPackageImplementationHelper getInstance() {
        return ServiceManager.getService(ROSPackageImplementationHelper.class);
    }

    public abstract Object[] getDirectoryCachedValueDependencies(ROSPackage pkg);

    public abstract GlobalSearchScope adjustAllScope(ROSPackage pkg, GlobalSearchScope scope);
}
