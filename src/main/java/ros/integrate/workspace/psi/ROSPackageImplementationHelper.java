package ros.integrate.workspace.psi;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.search.GlobalSearchScope;

public abstract class ROSPackageImplementationHelper {
    public static ROSPackageImplementationHelper getInstance() {
        return ServiceManager.getService(ROSPackageImplementationHelper.class);
    }

    public abstract Object[] getDirectoryCachedValueDependencies(ROSPackage pkg);

    public abstract GlobalSearchScope adjustAllScope(ROSPackage pkg, GlobalSearchScope scope);
}
