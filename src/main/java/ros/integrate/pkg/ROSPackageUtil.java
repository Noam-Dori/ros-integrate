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

class ROSPackageUtil {
    static boolean belongsToRoot(@NotNull VirtualFile root, @NotNull VirtualFile vFile) {
        return childOf(root.getPath(), vFile.getPath());
    }

    static boolean belongsToRoot(@NotNull PsiDirectory root, @NotNull VFileEvent event) {
        return belongsToRoot(root.getVirtualFile(), event);
    }

    static boolean belongsToRoot(@NotNull VirtualFile root, @NotNull VFileEvent event) {
        String rootPath = root.getPath();
        if (event instanceof VFileMoveEvent && childOf(rootPath, ((VFileMoveEvent) event).getOldPath())) {
            return true;
        }
        return childOf(rootPath, event.getPath());
    }

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

    static boolean belongToPackage(@NotNull ROSPackage pkg, PsiDirectory childDirectory) {
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
