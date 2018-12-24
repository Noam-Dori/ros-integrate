package ros.integrate.workspace;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

class ROSPackageUtil {
    static final String PACKAGE_XML = "package.xml";

    @Contract(pure = true)
    static boolean belongsToRoot(@NotNull PsiDirectory root, @NotNull VFileEvent event) {
        String rootPath = root.getVirtualFile().getPath();
        return getParentOfEvent(event).getPath().contains(rootPath);
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
            return event.getFile();
        } else return null;
    }
}
