package ros.integrate.workspace.unused.psi.impl;

import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.Processor;
import com.intellij.util.Processors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.psi.ROSPktFile;
import ros.integrate.workspace.psi.ROSPackage;
import ros.integrate.workspace.unused.psi.ROSPackageBase;
import ros.integrate.workspace.unused.psi.ROSPackageImplementationHelper;
import ros.integrate.workspace.unused.psi.ROSPsiFacade;

import java.util.ArrayList;
import java.util.Collection;

/**
 * represents a package the user took as a source.
 */
public class ROSPackageImpl extends ROSPackageBase {
    private volatile CachedValue<Collection<PsiDirectory>> myDirectories;
    private volatile CachedValue<Collection<PsiDirectory>> myDirectoriesWithLibSources;

    @Override
    protected Collection<PsiDirectory> getAllDirectories(boolean includeLibrarySources) {
        if (includeLibrarySources) {
            if (myDirectoriesWithLibSources == null) {
                myDirectoriesWithLibSources = createCachedDirectories(true);
            }
            return myDirectoriesWithLibSources.getValue();
        }
        else {
            if (myDirectories == null) {
                myDirectories = createCachedDirectories(false);
            }
            return myDirectories.getValue();
        }
    }

    @NotNull
    private CachedValue<Collection<PsiDirectory>> createCachedDirectories(final boolean includeLibrarySources) {
        return CachedValuesManager.getManager(getProject()).createCachedValue(() -> {
            Collection<PsiDirectory> result = new ArrayList<>();
            Processor<PsiDirectory> processor = Processors.cancelableCollectProcessor(result);
            getFacade().processPackageDirectories(this, allScope(), processor, includeLibrarySources);
            return CachedValueProvider.Result.create(result, ROSPackageImplementationHelper.getInstance().getDirectoryCachedValueDependencies(this));
        }, false);
    }

    @NotNull
    private GlobalSearchScope allScope() {
        return ROSPackageImplementationHelper.getInstance().adjustAllScope(this, GlobalSearchScope.allScope(getProject()));
    }

    @Override
    protected ROSPackage findPackage(String qName) {
        return getFacade().findPackage(qName);
    }

    public ROSPackageImpl(PsiManager manager, String qualifiedName) {
        super(manager, qualifiedName);
    }

    @NotNull
    @Override
    public ROSPktFile[] getPackets(@NotNull GlobalSearchScope scope) {
        return getFacade().getPackets(this,scope);
    }

    @NotNull
    @Override
    public PsiFile[] getSources(@NotNull GlobalSearchScope scope) {
        return getFacade().getSources(this,scope);
    }

    @Nullable
    @Override
    public PsiFile getCMakeLists() {
        return getFacade().getCmakeLists(this);
    }

    @NotNull
    @Override
    public XmlFile getPackageXml() {
        return getFacade().getPackageXml(this);
    }

    @NotNull
    @Override
    public PsiFile[] getFiles(@NotNull GlobalSearchScope scope) {
        return getFacade().getFilesInPackage(this,scope);
    }

    @Nullable
    @Override
    public PsiModifierList getModifierList() {
        return null;
    }

    @Override
    public boolean hasModifierProperty(@NotNull String name) {
        return false;
    }

    @NotNull
    private ROSPsiFacade getFacade() {
        return ROSPsiFacade.getInstance(getProject());
    }
}
