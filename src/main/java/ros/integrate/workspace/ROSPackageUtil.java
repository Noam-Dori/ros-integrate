package ros.integrate.workspace;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.events.VFileCopyEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileMoveEvent;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.workspace.psi.ROSPackage;

import java.util.Objects;

class ROSPackageUtil {
    static final String PACKAGE_XML = "package.xml";

    @Contract(pure = true)
    static boolean belongsToRoot(@NotNull PsiDirectory root, @NotNull VFileEvent event) {
        String rootPath = root.getVirtualFile().getPath();
        if(event instanceof VFileMoveEvent && ((VFileMoveEvent) event).getOldPath().contains(rootPath)) {
            return true;
        }
        return event.getPath().contains(rootPath);
    }

    @NotNull
    static VirtualFile getParentOfEvent(@NotNull VFileEvent event) {
        return event instanceof VFileCreateEvent ? ((VFileCreateEvent) event).getParent()
                : Objects.requireNonNull(event.getFile()).getParent();
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
        if(isPackageXml(event)) {
            return event instanceof VFileCopyEvent ?
                    ((VFileCopyEvent) event).getNewParent().findChild(((VFileCopyEvent) event).getNewChildName())
                    : event.getFile();
        } else {
            return null;
        }
    }

    @Nullable
    static XmlFile findPackageXml(@NotNull PsiDirectory root) {
        for(PsiFile file : root.getFiles()) {
            if(file.getName().equals(PACKAGE_XML)) {
                return (XmlFile) file;
            }
        }
        return null;
    }

    static int getRequiredSorts(VFileEvent event, PsiDirectory currentRoot) {
        if(event instanceof VFileMoveEvent) {
            String rootPath = currentRoot.getVirtualFile().getPath();
            if(((VFileMoveEvent) event).getOldPath().contains(rootPath)
                    && event.getPath().contains(rootPath)) {
                return 1;
            }
            return 2;
        }
        return 1;
    }

    static boolean belongToPackage(@NotNull ROSPackage pkg, PsiDirectory childDirectory) {
        for (PsiDirectory root : pkg.getRoots()) {
            if(childDirectory.getVirtualFile().getPath().contains(root.getVirtualFile().getPath())) {
                return true;
            }
        }
        return false;
    }
}
