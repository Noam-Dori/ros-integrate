package ros.integrate.pkg;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ROSIndexChangeListener implements BulkFileListener {
    @NotNull
    private final Project project;

    public ROSIndexChangeListener(@NotNull Project project) {
        this.project = project;
    }

    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
        project.getService(ROSPackageManager.class).filesChanged(events);
    }


}
