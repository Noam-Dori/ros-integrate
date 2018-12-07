package ros.integrate.workspace;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.SortedList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.workspace.psi.ROSPackage;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class ROSPackageManagerImpl implements ROSPackageManager {
    private final ConcurrentMap<String, ROSPackage> pkgCache = ContainerUtil.createConcurrentSoftValueMap();
    private final Project project;

    public ROSPackageManagerImpl(@NotNull Project project) {
        this.project = project;
        findAndCachePackages();
        // add a watch to VirtualFileSystem that will trigger this
        project.getMessageBus().connect().subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
            @Override
            public void before(@NotNull List<? extends VFileEvent> events) {
                doBulkFileChangeEvents(events);
            }
        });
    }

    private void doBulkFileChangeEvents(List<? extends VFileEvent> events) {
        // 1. figure out which package these files belongs to, and map events per package
        // options - none, and a specific package
        // 2. do action based on event type
        //TODO
    }

    @Override
    public List<ROSPackage> getAllPackages() {
        final SortedList<ROSPackage> ret = new SortedList<>(Comparator.comparing(ROSPackage::getQualifiedName));
        pkgCache.forEach((name,pkg) -> ret.add(pkg));
        return ret;
    }

    @Nullable
    @Override
    public ROSPackage findPackage(String pkgName) {
        return pkgCache.get(pkgName);
    }
}
