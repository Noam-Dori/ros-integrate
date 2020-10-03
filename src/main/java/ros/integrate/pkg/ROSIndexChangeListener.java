package ros.integrate.pkg;

import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * a general file event listener that triggers a package index update when files change somehow.
 * This also refreshes the library dependencies since CLion is mean and deletes the dependencies every reload.
 * However, this is a band-aid, not a real fix. It does not work in 2019.3. It works in CLion 2020.2,
 * but it could break once again in the future.
 * @author Noam Dori
 */
public class ROSIndexChangeListener implements BulkFileListener {
    @NotNull
    private final Project project;

    /**
     * construct the file event listener
     * @param project the project this listener belongs to
     */
    public ROSIndexChangeListener(@NotNull Project project) {
        this.project = project;
    }

    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
        Arrays.stream(ModuleManager.getInstance(project).getModules())
                .forEach(module -> ROSPackageFinder.FINDERS.forEach(finder -> finder.setDependency(module)));
        project.getService(ROSPackageManager.class).filesChanged(events);
    }


}
