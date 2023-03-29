package ros.integrate.cmake;

import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.move.moveFilesOrDirectories.MoveFileHandler;
import com.intellij.usageView.UsageInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.cmake.adapter.CMakeFileAdapter;
import ros.integrate.cmake.psi.CMakeFile;
import ros.integrate.pkg.ROSPackageManager;
import ros.integrate.pkg.psi.ROSPackage;

import java.util.List;
import java.util.Map;

public class MoveCMakeFileHandler extends MoveFileHandler {
    @Override
    public boolean canProcessElement(PsiFile element) {
        return element instanceof CMakeFile;
    }

    @Override
    public void prepareMovedFile(@NotNull PsiFile rawFile, PsiDirectory moveDestination, Map<PsiElement, PsiElement> oldToNewMap) {
        ROSPackage newPkg = rawFile.getProject().getService(ROSPackageManager.class).findPackage(moveDestination);
        if (newPkg != null) {
            new CMakeFileAdapter(rawFile).setPackage(newPkg);
        }
    }

    @Override
    public @Nullable List<UsageInfo> findUsages(PsiFile psiFile, PsiDirectory newParent, boolean searchInComments, boolean searchInNonJavaFiles) {
        return null;
    }

    @Override
    public void retargetUsages(List<UsageInfo> usages, Map<PsiElement, PsiElement> oldToNewMap) {}

    @Override
    public void updateMovedFile(PsiFile file) {}
}
