package ros.integrate.pkg;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.events.VFileCopyEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileMoveEvent;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.psi.ROSPackage;

/**
 * a utility class that contains commonly used functions related to ROS packages
 */
class ROSPackageUtil {
    /**
     * checks if the virtual file is a decedent of another file. This check is "recursive"
     * @param root the parent virtual file to check against
     * @param vFile the child virtual file to check against
     * @return true if root contains vFile directly in indirectly, false otherwise
     */
    static boolean belongsToRoot(@NotNull VirtualFile root, @NotNull VirtualFile vFile) {
        return childOf(root.getPath(), vFile.getPath());
    }

    /**
     * checks if the virtual file affected in the event is a decedent of another directory. This check is "recursive"
     * @param root the parent directory to check against
     * @param event the event that affected the child virtual file
     * @return true if root contains vFile directly in indirectly, false otherwise
     */
    static boolean belongsToRoot(@NotNull PsiDirectory root, @NotNull VFileEvent event) {
        return belongsToRoot(root.getVirtualFile(), event);
    }

    /**
     * checks if the virtual file affected in the event is a decedent of another file. This check is "recursive"
     * @param root the parent virtual file to check against
     * @param event the event that affected the child virtual file
     * @return true if root contains vFile directly in indirectly, false otherwise
     */
    static boolean belongsToRoot(@NotNull VirtualFile root, @NotNull VFileEvent event) {
        String rootPath = root.getPath();
        if (event instanceof VFileMoveEvent && childOf(rootPath, ((VFileMoveEvent) event).getOldPath())) {
            return true;
        }
        return childOf(rootPath, event.getPath());
    }

    /**
     * counts how many times an event needs to be checked against affected packages. Used when updating the index
     * @param event the event to check
     * @param currentRoot the directory to check against
     * @return 2 if the event is a move event that moved a file out of "currentRoot", 1 otherwise
     */
    static int getRequiredSorts(VFileEvent event, PsiDirectory currentRoot) {
        if (event instanceof VFileMoveEvent) {
            String rootPath = currentRoot.getVirtualFile().getPath();
            if (childOf(rootPath, ((VFileMoveEvent) event).getOldPath()) && childOf(rootPath, event.getPath())) {
                return 1;
            }
            return 2;
        }
        return 1;
    }

    /**
     * checks if a directory is part of a ROS package
     * @param pkg the ROS package to check against
     * @param childDirectory the directory to check
     * @return true if the directory is a child of the package directly or indirectly, false otherwise.
     */
    static boolean belongToPackage(@NotNull ROSPackage pkg, @NotNull PsiDirectory childDirectory) {
        for (PsiDirectory root : pkg.getRoots()) {
            if (childOf(root.getVirtualFile().getPath(), childDirectory.getVirtualFile().getPath())) {
                return true;
            }
        }
        return false;
    }

    private static boolean childOf(@NotNull String parent, @NotNull String child) {
        if (!child.startsWith(parent)) {
            return false;
        }
        String diff = child.substring(parent.length());
        return diff.isEmpty() || diff.startsWith("/");
    }

    /**
     * gets the parent virtual file of the event
     * @param event the event to get the parent of
     * @return a virtual file representing the parent directory of the file this event affects
     */
    @Nullable
    static VirtualFile getParentOfEvent(VFileEvent event) {
        VirtualFile ret;
        if (event instanceof VFileCreateEvent) {
            ret = ((VFileCreateEvent) event).getParent();
        }
        else if (event instanceof VFileMoveEvent) {
            ret = ((VFileMoveEvent) event).getNewParent();
        }
        else if (event instanceof VFileCopyEvent) {
            ret = ((VFileCopyEvent) event).getNewParent();
        }
        else {
            ret = event.getFile();
            if (ret != null && ret.getParent() != null) {
                ret = ret.getParent();
            }
        }
        if (ret == null) {
            ret = event.getFile();
        }
        return ret;
    }
}
