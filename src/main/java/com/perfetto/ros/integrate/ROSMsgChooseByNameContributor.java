package com.perfetto.ros.integrate;

import com.intellij.lang.ASTNode;
import com.intellij.lang.TraverserBasedASTNode;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.perfetto.ros.integrate.psi.ROSMsgFile;
import com.perfetto.ros.integrate.psi.ROSMsgProperty;
import com.perfetto.ros.integrate.psi.impl.ROSMsgPropertyImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ROSMsgChooseByNameContributor implements ChooseByNameContributor {
    @NotNull
    @Override
    public String[] getNames(Project project, boolean includeNonProjectItems) {
        List<String> names = ROSMsgUtil.findProjectMsgNames(project);
        return names.toArray(new String[0]);
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        //TODO: include non project items
        List<ROSMsgFile> properties = ROSMsgUtil.findProjectMsgLocations(project, name, null);
        //noinspection SuspiciousToArrayCall
        return properties.toArray(new NavigationItem[0]);
    }
}