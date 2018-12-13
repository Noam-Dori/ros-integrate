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

    private void findAndCachePackages() {
        // 1. get all package finders (new EP)
        List<ROSPackageFinder> finders = ROSPackageFinder.EP_NAME.getExtensionList();
        // 2. use each package finder to make and cache packages
        finders.forEach(finder -> finder.findAndCache(project,pkgCache));
    }

    private void doBulkFileChangeEvents(List<? extends VFileEvent> events) {
        // 1. filter by file (if these are key-files (CMakeLists.txt or package.xml) and by project (if the file belongs to this project)
        // 2. group by parent dir (convert to package if possible)
        // 3. figure out what happened per package per file
        // 4. do steps (create new package, delete new package, modify details)
        //TODO
    }

    @Override
    public List<ROSPackage> getAllPackages() {
        final SortedList<ROSPackage> ret = new SortedList<>(Comparator.comparing(ROSPackage::getName));
        pkgCache.forEach((name,pkg) -> ret.add(pkg));
        return ret;
    }

    @Nullable
    @Override
    public ROSPackage findPackage(String pkgName) {
        return pkgCache.get(pkgName);
    }

    @Override
    public void updatePackageName(ROSPackage pkg, String newName) {
        pkgCache.remove(pkg.getName());
        pkgCache.put(newName,pkg);
    }
}
