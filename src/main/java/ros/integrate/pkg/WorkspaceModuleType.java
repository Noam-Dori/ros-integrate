package ros.integrate.pkg;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class WorkspaceModuleType extends ModuleType<WorkspaceModuleBuilder> {
    private static final String ID = "WORKSPACE_MODULE_TYPE";

    protected WorkspaceModuleType() {
        super(ID);
    }

    static WorkspaceModuleType getInstance() {
        return (WorkspaceModuleType) ModuleTypeManager.getInstance().findByID(ID);
    }

    @NotNull
    @Override
    public WorkspaceModuleBuilder createModuleBuilder() {
        return new WorkspaceModuleBuilder();
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public @NotNull String getName() {
        return "ROS Workspace Module";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @Override
    public @NotNull String getDescription() {
        return "A module for ROS workspaces";
    }

    @Override
    public @NotNull Icon getNodeIcon(boolean isOpened) {
        return AllIcons.Nodes.Package; // icons.jar/nodes/package.svg
    }
}
