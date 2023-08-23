package ros.integrate.pkg;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.settings.ROSSettings;
import ros.integrate.ui.HistoryKey;

import java.util.Arrays;

/**
 * a startup activity that triggers the creation/loading of ROS libraries,
 * and indexes all the packages in a non-cancelling action.
 * The libraries are critical to be loaded first as the index relies on a
 * base that extends further than the project alone.
 * @author Noam Dori
 */
public class LoadROSLibrariesAndIndex implements ProjectActivity {

    @Nullable
    @Override
    public Continuation<? super Unit> execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        ROSPackageFinder.FINDERS.forEach(finder -> WriteCommandAction.runWriteCommandAction(project, () -> {
            finder.loadLibraries(project);
            Arrays.asList(ModuleManager.getInstance(project).getModules()).forEach(finder::setDependency);
        }));
        ROSSettings.getInstance(project).addListener(settings -> WriteCommandAction.runWriteCommandAction(project, () -> {
            if (ROSPackageFinder.FINDERS.stream().map(finder -> finder.updateLibraries(project))
                    .reduce((sum, val) -> sum || val).orElse(false))
                project.getService(ROSPackageManager.class).invalidateIndex();
        }), new String[]{HistoryKey.EXTRA_SOURCES.get(),
                HistoryKey.WORKSPACE.get()});
        ROSPackageManager.getInstance(project).getAllPackages(); // just loads the index
        return continuation;
    }
}
