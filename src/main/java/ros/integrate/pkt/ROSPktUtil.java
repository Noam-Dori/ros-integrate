package ros.integrate.pkt;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.psi.ROSMsgFile;
import ros.integrate.pkt.psi.ROSPktComment;
import ros.integrate.pkt.psi.ROSPktElementFactory;
import ros.integrate.workspace.ROSPackageManager;
import ros.integrate.workspace.psi.ROSPackage;

import java.util.*;

/**
 * a generic utility class regarding ROS messages and services.
 */
public class ROSPktUtil {
    /**
     * checks of this is an annotation comment that contains the specified id.
     * @param comment the psi-element to check
     * @param id the string ID of the inspection
     * @return {@param comment} in {@link ROSPktComment} form if it is an annotation, <code>null</code> otherwise.
     */
    @Nullable
    @Contract("null, _ -> null")
    public static ROSPktComment checkAnnotation(@Nullable PsiElement comment, String id) {
        if (comment instanceof ROSPktComment &&
                comment.getText().startsWith(ROSPktElementFactory.ANNOTATION_PREFIX)
                && (id == null || comment.getText().substring(ROSPktElementFactory.ANNOTATION_PREFIX.length() - 1)
                .contains(id))) {
            return (ROSPktComment) comment;
        }
        return null;
    }

    /**
     * finds all the message files in the provided project with additional options for filtering
     * @param project the project where to search for all messages
     * @param pkgName if null, will search for all packages.
     *                If not null, will search for a package with this name and if it is not found will return an empty list.
     * @param exclude if null, will search for all files in the project.
     *                If not null, the provided file will be excluded from the search.
     * @return a non-null list containing all message files found via the query.
     */
    static List<ROSMsgFile> findMessages(@NotNull Project project, @Nullable String pkgName, @Nullable ROSMsgFile exclude) {
        ROSPackageManager manager = project.getComponent(ROSPackageManager.class);
        List<ROSMsgFile> result = new ArrayList<>();
        List<ROSPackage> packages;
        if(pkgName == null){
            packages = manager.getAllPackages();
        } else {
            packages = new ArrayList<>();
            Optional.ofNullable(manager.findPackage(pkgName)).ifPresent(packages::add);
        }
        for (ROSPackage pkg : packages) {
            Arrays.stream(pkg.getPackets(GlobalSearchScope.allScope(project)))
                    .filter(pkt -> pkt instanceof ROSMsgFile && !pkt.equals(exclude))
                    .map(pkt -> (ROSMsgFile)pkt)
                    .forEach(result::add);
        }
        return result.isEmpty() ? Collections.emptyList() : result;
    }

    /**
     * finds all the message files in the provided project with additional options for filtering
     * @param project the project where to search for all messages
     * @param pkgName the name of the package this message belongs to
     * @param msgName the name of the message file to search for
     * @return null if no message was found with the given scope
     */
    @Nullable
    public static ROSMsgFile findMessage(@NotNull Project project, @NotNull String pkgName, @NotNull String msgName) {
        ROSPackageManager manager = project.getComponent(ROSPackageManager.class);
        final ROSPackage pkg = manager.findPackage(pkgName);
        return pkg != null ? pkg.findPacket(msgName, ROSMsgFile.class) : null;
    }

    /**
     * a useful utility function for trimming the .msg or .srv from the message file name.
     * @param name the string holding the message/service file name.
     * @return the trimmed version of the provided string.
     */
    @NotNull
    public static String trimPktFileName(@NotNull String name) {
        return name.substring(0,name.lastIndexOf('.'));
    }
}
