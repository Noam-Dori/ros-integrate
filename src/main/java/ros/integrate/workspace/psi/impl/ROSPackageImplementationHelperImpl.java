package ros.integrate.workspace.psi.impl;

import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.NonClasspathClassFinder;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiModificationTracker;
import ros.integrate.workspace.psi.ROSPackage;
import ros.integrate.workspace.psi.ROSPackageImplementationHelper;

public class ROSPackageImplementationHelperImpl extends ROSPackageImplementationHelper {
    @Override
    public Object[] getDirectoryCachedValueDependencies(ROSPackage pkg) {
        return new Object[] { PsiModificationTracker.OUT_OF_CODE_BLOCK_MODIFICATION_COUNT, ProjectRootManager.getInstance(pkg.getProject()) };
    }

    @Override
    public GlobalSearchScope adjustAllScope(ROSPackage pkg, GlobalSearchScope scope) {
        return NonClasspathClassFinder.addNonClasspathScope(pkg.getProject(), scope);
    }
}
