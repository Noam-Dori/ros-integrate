package ros.integrate.elementFinder.impl;

import com.intellij.openapi.application.ReadActionProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.Processor;
import ros.integrate.workspace.unused.psi.ROSPackageIndex;
import ros.integrate.elementFinder.ROSPsiElementFinder;
import ros.integrate.pkt.psi.ROSPktFile;
import ros.integrate.workspace.psi.ROSPackage;

public class ROSPsiSourceElementFinder extends ROSPsiElementFinder {
    private final Project myProject;

    public ROSPsiSourceElementFinder(Project project) {
        myProject = project;
    }

    @Override
    public boolean processPackageDirectories(ROSPackage pkg, GlobalSearchScope scope, Processor<PsiDirectory> processor, boolean includeLibrarySources) {
        final PsiManager psiManager = PsiManager.getInstance(myProject);
        return ROSPackageIndex.getInstance(myProject)
                .getDirsByPackageName(pkg.getQualifiedName(), includeLibrarySources)
                .forEach(new ReadActionProcessor<VirtualFile>() {
                    @Override
                    public boolean processInReadAction(final VirtualFile dir) {
                        if (!scope.contains(dir)) return true;
                        PsiDirectory psiDir = psiManager.findDirectory(dir);
                        return psiDir == null || processor.process(psiDir);
                    }
                });
    }

    @Override
    public ROSPackage findPackage(String qualifiedName) {
        return null;
    }

    @Override
    public Condition<PsiFile> getPackageFilesFilter(ROSPackage pkg, GlobalSearchScope scope) {
        return null;
    }

    @Override
    public PsiFile[] getPackageFiles(ROSPackage pkg, GlobalSearchScope scope) {
        return new PsiFile[0];
    }

    @Override
    public Condition<ROSPktFile> getPacketsFilter(GlobalSearchScope scope) {
        return null;
    }

    @Override
    public ROSPktFile[] getPackets(ROSPackage pkg, GlobalSearchScope scope) {
        return new ROSPktFile[0];
    }

    @Override
    public Condition<PsiFile> getSourcesFilter(GlobalSearchScope scope) {
        return null;
    }

    @Override
    public PsiFile[] getSources(ROSPackage pkg, GlobalSearchScope scope) {
        return new PsiFile[0];
    }

    @Override
    public PsiFile getCMakeLists(ROSPackage pkg) {
        return null;
    }

    @Override
    public XmlFile getPackageXml(ROSPackage pkg) {
        return null;
    }
}
