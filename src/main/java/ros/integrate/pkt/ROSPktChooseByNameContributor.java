package ros.integrate.pkt;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import ros.integrate.pkt.psi.ROSMsgFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// unused for now.
public class ROSPktChooseByNameContributor implements ChooseByNameContributor {
    @NotNull
    @Override
    public String[] getNames(Project project, boolean includeNonProjectItems) {
        List<String> names = ROSPktUtil.findMessageNames(project,null,null);
        return names.toArray(new String[0]);
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        //TODO: include non project items
        List<ROSMsgFile> fields = ROSPktUtil.findMessages(project, name, null);
        //noinspection SuspiciousToArrayCall
        return fields.toArray(new NavigationItem[0]);
    }
}