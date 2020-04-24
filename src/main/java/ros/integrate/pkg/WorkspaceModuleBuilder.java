package ros.integrate.pkg;

import com.intellij.ide.util.projectWizard.ModuleBuilder;

public class WorkspaceModuleBuilder extends ModuleBuilder {
    @Override
    public WorkspaceModuleType getModuleType() {
        return WorkspaceModuleType.getInstance();
    }
}
