package ros.integrate.settings;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * a collection of utility functions for handling plugin configurations
 * @author Noam Dori
 */
class ROSSettingsUtil {
    private static final List<String> WORKSPACE_NAMES = Arrays.asList(".catkin_workspace", ".catkin_tools");
    /**
     * attempts to find the user's workspace directory for him
     * @param project the project currently open
     * @return null if the plugin could not find the user's workspace directory,
     * otherwise the absolute path to the user's workspace directory.
     */
    @Nullable
    static String detectWorkspace(@NotNull Project project) {
        GlobalSearchScope scope = GlobalSearchScope.projectScope(project);
        for (String name : WORKSPACE_NAMES) {
            for (VirtualFile file : FilenameIndex.getVirtualFilesByName(name, true, scope)) {
                return file.getParent().getPath();
            }
        }
        // attempts finding parent "above" project.
        VirtualFile[] roots = ProjectRootManager.getInstance(project).getContentRoots();
        if (roots.length == 0) {
            return null;
        }
        VirtualFile treeNode = roots[0];
        while (treeNode != null) {
            for (String name : WORKSPACE_NAMES) {
                if (treeNode.findChild(name) != null) {
                    return treeNode.getPath();
                }
            }
            treeNode = treeNode.getParent();
        }
        return null;
    }
}
