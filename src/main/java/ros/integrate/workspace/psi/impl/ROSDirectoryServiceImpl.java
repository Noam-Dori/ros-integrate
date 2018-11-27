package ros.integrate.workspace.psi.impl;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.workspace.psi.ROSDirectoryService;
import ros.integrate.workspace.psi.ROSPackage;
import ros.integrate.workspace.psi.ROSPsiFacade;

public class ROSDirectoryServiceImpl extends ROSDirectoryService {
    @Nullable
    @Override
    public ROSPackage getPackage(@NotNull PsiDirectory dir) {
        Project project = dir.getProject();
        ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(project).getFileIndex();
        VirtualFile virtualFile = dir.getVirtualFile();
        String packageName = projectFileIndex.getPackageNameByDirectory(virtualFile);
        if (packageName == null) return null;
        return ROSPsiFacade.getInstance(project).findPackage(packageName);
    }
}
