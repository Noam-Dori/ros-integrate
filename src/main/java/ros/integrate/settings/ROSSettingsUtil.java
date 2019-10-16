package ros.integrate.settings;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ROSSettingsUtil {
    @Nullable
    static String detectWorkspace(@NotNull Project project) {
        PsiFile[] files = FilenameIndex.getFilesByName(project,".catkin_workspace", GlobalSearchScope.projectScope(project));
        if(files.length > 0) {
            return files[0].getContainingDirectory().getVirtualFile().getPath();
        }
        // attempts finding parent "above" project.
        VirtualFile treeNode = FilenameIndex.getFilesByName(project,FilenameIndex.getAllFilenames(project)[0],
                GlobalSearchScope.projectScope(project))[0].getContainingDirectory().getVirtualFile();
        while (treeNode != null) {
            if (treeNode.findChild(".catkin_workspace") != null) {
                return treeNode.getPath();
            }
            treeNode = treeNode.getParent();
        }
        return null;
    }
}
