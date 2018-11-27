package ros.integrate.workspace.psi;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NotNullLazyKey;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.psi.ROSPktFile;

public abstract class ROSPsiFacade {
    private static final NotNullLazyKey<ROSPsiFacade, Project> INSTANCE_KEY = ServiceManager.createLazyKey(ROSPsiFacade.class);

    @NotNull
    public static ROSPsiFacade getInstance(Project project) {
        return INSTANCE_KEY.getValue(project);
    }

    protected abstract Project getProject();

    /**
     * Searches the project for the package with the specified full-qualified name and returns one
     * if it is found.
     *
     * @param qualifiedName the full-qualified name of the package to find.
     * @return the PSI package, or null if no package with such name is found.
     */
    @Nullable
    public abstract ROSPackage findPackage(@NonNls @NotNull String qualifiedName);

    public abstract PsiFile[] getFilesInPackage(ROSPackage pkg, GlobalSearchScope scope);

    public abstract boolean processPackageDirectories(ROSPackage pkg, GlobalSearchScope scope, Processor<PsiDirectory> processor, boolean includeLibrarySources);

    public abstract ROSPktFile[] getPackets(ROSPackage pkg, GlobalSearchScope scope);

    public abstract PsiFile[] getSources(ROSPackage pkg, GlobalSearchScope scope);

    public abstract PsiFile getCmakeLists(ROSPackage pkg);

    public abstract XmlFile getPackageXml(ROSPackage pkg);
}
