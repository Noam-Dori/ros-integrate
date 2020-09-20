package ros.integrate.pkg;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;
import ros.integrate.settings.BrowserOptions;
import ros.integrate.settings.ROSSettings;

import java.util.Arrays;

/**
 * a startup activity trigger the creation/load of ROS libraries
 */
public class ROSLibraryBuilder implements StartupActivity {

    @Override
    public void runActivity(@NotNull Project project) {
        ROSPackageFinder.FINDERS.forEach(finder -> WriteCommandAction.runWriteCommandAction(project, () -> {
            finder.loadLibraries(project);
            Arrays.asList(ModuleManager.getInstance(project).getModules()).forEach(finder::setDependency);
        }));
        ROSSettings.getInstance(project).addListener(settings -> WriteCommandAction.runWriteCommandAction(project, () -> {
            if (ROSPackageFinder.FINDERS.stream().map(finder -> finder.updateLibraries(project))
                    .reduce((sum, val) -> sum || val).orElse(false))
                project.getService(ROSPackageManager.class).reloadIndex();
        }), new String[]{BrowserOptions.HistoryKey.EXTRA_SOURCES.get(),
                BrowserOptions.HistoryKey.WORKSPACE.get()});
    }
}
