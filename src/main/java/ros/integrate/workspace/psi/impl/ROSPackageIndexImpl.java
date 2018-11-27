package ros.integrate.workspace.psi.impl;

import com.intellij.openapi.roots.impl.DirectoryIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;
import ros.integrate.workspace.psi.ROSPackageIndex;

public class ROSPackageIndexImpl extends ROSPackageIndex {
    private final DirectoryIndex myDirectoryIndex;

    public ROSPackageIndexImpl(DirectoryIndex directoryIndex) {
        myDirectoryIndex = directoryIndex;
    }

    @NotNull
    @Override
    public VirtualFile[] getDirectoriesByPackageName(@NotNull String packageName, boolean includeLibrarySources) {
        return getDirsByPackageName(packageName, includeLibrarySources).toArray(VirtualFile.EMPTY_ARRAY);
    }

    @NotNull
    @Override
    public Query<VirtualFile> getDirsByPackageName(@NotNull String packageName, boolean includeLibrarySources) {
        return myDirectoryIndex.getDirectoriesByPackageName(packageName, includeLibrarySources);
    }
}
