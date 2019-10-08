package ros.integrate.workspace;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.psi.ROSPktFile;
import ros.integrate.workspace.psi.ROSPackage;
import ros.integrate.workspace.psi.impl.ROSSourcePackage;

import java.util.List;

/**
 * a default finder used for finding packages within the project
 */
public class ROSProjectPackageFinder extends ROSPackageFinderBase {
    private static final Logger LOG = Logger.getInstance("#ros.integrate.workspace.ROSProjectPackageFinder");

    @Override
    Class<? extends ROSPackage> getPackageType() {
        return ROSSourcePackage.class;
    }

    @NotNull
    @Override
    ROSPackage tryNewROSPackage(Project project, String pkgName, PsiDirectory xmlRoot, XmlFile pkgXml, List<ROSPktFile> pkgPackets) {
        ROSPackage newPkg = new ROSSourcePackage(project, pkgName, xmlRoot, pkgXml, pkgPackets);
        if (newPkg == ROSPackage.ORPHAN) {
            LOG.error("Failed indexing a valid ROS package",
                    "the project package finder tried finding a ros package, and failed.",
                    "Name: [" + pkgName + "]",
                    "Root: [" + xmlRoot.getVirtualFile().getPath() + "]");
        }
        return newPkg;
    }

    @NotNull
    @Override
    GlobalSearchScope getScope(Project project) {
        return GlobalSearchScope.projectScope(project);
    }

    @Nullable
    @Override
    public Library getLibrary(Project project) {
        return null;
    }

    boolean notInFinder(@NotNull VirtualFile vFile, @NotNull Project project) {
        return !ProjectRootManager.getInstance(project).getFileIndex().isInContent(vFile);
    }

    boolean inFinder(@NotNull VFileEvent event, @NotNull Project project) {
        return ProjectRootManager.getInstance(project).getFileIndex().isInContent(ROSPackageUtil.getParentOfEvent(event));
    }
}
