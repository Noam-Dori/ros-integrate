package ros.integrate.settings;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ROSSettingsUtil {
    @Nullable
    static String detectWorkspace(@NotNull Project project) {
        GlobalSearchScope scope = GlobalSearchScope.projectScope(project);
        PsiFile[] files = FilenameIndex.getFilesByName(project, ".catkin_workspace", scope);
        if (files.length > 0) {
            return files[0].getContainingDirectory().getVirtualFile().getPath();
        }
        // attempts finding parent "above" project.
        VirtualFile[] roots = ProjectRootManager.getInstance(project).getContentRoots();
        if (roots.length == 0) {
            return null;
        }
        VirtualFile treeNode = roots[0];
        while (treeNode != null) {
            if (treeNode.findChild(".catkin_workspace") != null) {
                return treeNode.getPath();
            }
            treeNode = treeNode.getParent();
        }
        return null;
    }
}
