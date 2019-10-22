package ros.integrate.workspace;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.events.VFileCopyEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileMoveEvent;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.workspace.psi.ROSPackage;

import java.util.Objects;

class ROSPackageUtil {
    static final String PACKAGE_XML = "package.xml";

    static boolean belongsToRoot(@NotNull VirtualFile root, @NotNull VirtualFile vFile) {
        return childOf(root.getPath(), vFile.getPath());
    }

    static boolean belongsToRoot(@NotNull PsiDirectory root, @NotNull VFileEvent event) {
        return belongsToRoot(root.getVirtualFile(), event);
    }

    static boolean belongsToRoot(@NotNull VirtualFile root, @NotNull VFileEvent event) {
        String rootPath = root.getPath();
        if (event instanceof VFileMoveEvent && childOf(((VFileMoveEvent) event).getOldPath(), rootPath)) {
            return true;
        }
        return childOf(event.getPath(), rootPath);
    }

    static boolean isPackageXml(@NotNull VFileEvent event) {
        return getEventName(event).equals(PACKAGE_XML);
    }

    private static String getEventName(@NotNull VFileEvent event) {
        return event instanceof VFileCreateEvent ? ((VFileCreateEvent) event).getChildName()
                : Objects.requireNonNull(event.getFile()).getName();
    }


    @Nullable
    static VirtualFile getXml(@NotNull VFileEvent event) {
        if (isPackageXml(event)) {
            return event instanceof VFileCopyEvent ?
                    ((VFileCopyEvent) event).getNewParent().findChild(((VFileCopyEvent) event).getNewChildName())
                    : event.getFile();
        } else {
            return null;
        }
    }

    @Nullable
    static XmlFile findPackageXml(@NotNull PsiDirectory root) {
        for (PsiFile file : root.getFiles()) {
            if (file.getName().equals(PACKAGE_XML)) {
                return (XmlFile) file;
            }
        }
        return null;
    }

    static int getRequiredSorts(VFileEvent event, PsiDirectory currentRoot) {
        if (event instanceof VFileMoveEvent) {
            String rootPath = currentRoot.getVirtualFile().getPath();
            if (childOf(((VFileMoveEvent) event).getOldPath(), rootPath) && childOf(event.getPath(), rootPath)) {
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
}
