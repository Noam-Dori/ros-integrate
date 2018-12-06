package ros.integrate.workspace.unused.psi;

import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiBundle;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiDirectoryContainer;
import com.intellij.psi.PsiNameHelper;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.java.JavaModuleSourceRootTypes;
import ros.integrate.workspace.psi.ROSPackage;
import ros.integrate.workspace.unused.psi.impl.ROSDirectoryImpl;

public class ROSDirectoryFactory extends PsiDirectoryFactory {
    private final PsiManagerImpl myManager;

    public ROSDirectoryFactory(final PsiManagerImpl manager) {
        myManager = manager;
    }

    @NotNull
    @Override
    public PsiDirectory createDirectory(@NotNull final VirtualFile file) {
        return new ROSDirectoryImpl(myManager, file);
    }

    @Override
    @NotNull
    public String getQualifiedName(@NotNull final PsiDirectory directory, final boolean presentable) {
        final ROSPackage aPackage = ROSDirectoryService.getInstance().getPackage(directory);
        if (aPackage != null) {
            final String qualifiedName = aPackage.getQualifiedName();
            if (!qualifiedName.isEmpty()) return qualifiedName;
            if (presentable) {
                return PsiBundle.message("default.package.presentation") + " (" + directory.getVirtualFile().getPresentableUrl() + ")";
            }
            return "";
        }
        return presentable ? StringUtil.notNullize(FileUtil.getLocationRelativeToUserHome(directory.getVirtualFile().getPresentableUrl()), "") : "";
    }

    @Nullable
    @Override
    public PsiDirectoryContainer getDirectoryContainer(@NotNull PsiDirectory directory) {
        return ROSDirectoryService.getInstance().getPackage(directory);
    }

    @Override
    public boolean isPackage(@NotNull PsiDirectory directory) {
        ProjectFileIndex fileIndex = ProjectRootManager.getInstance(myManager.getProject()).getFileIndex();
        VirtualFile virtualFile = directory.getVirtualFile();
        return fileIndex.isUnderSourceRootOfType(virtualFile, JavaModuleSourceRootTypes.SOURCES) && fileIndex.getPackageNameByDirectory(virtualFile) != null;
    }

    @Override
    public boolean isValidPackageName(String name) {
        return PsiNameHelper.getInstance(myManager.getProject()).isQualifiedName(name);
    }
}
