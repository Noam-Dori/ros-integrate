package ros.integrate.pkg.psi.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.ROSIcons;
import ros.integrate.pkt.psi.ROSPktFile;

import javax.swing.*;
import java.util.Collection;
import java.util.Map;

/**
 * represents packages that are directly installed. There are pre-compiled packages that were already organised by the
 * build system. These usually exist in the ROS root directory (or "ROS Path")
 * @author Noam Dori
 */
public class ROSCompiledPackage extends ROSPackageBase {

    @NotNull
    private final Map<RootType,PsiDirectory> rootMap;

    /**
     * construct a new package (this class is abstract however)
     * @param project the project this package belongs to
     * @param name the name of the package. This is the fully qualifies name as well, used for indexing.
     * @param pkgXml the reference package.xml file
     * @param rootMap maps between different root directory types to the real PSI directory.
     * @param packets the collection of all packet files that belong to this package
     */
    public ROSCompiledPackage(@NotNull Project project, @NotNull String name, @NotNull Map<RootType, PsiDirectory> rootMap,
                            @NotNull XmlFile pkgXml, @NotNull Collection<ROSPktFile> packets) {
        super(project,name,pkgXml);
        this.rootMap = rootMap;
        addPackets(packets);
    }

    @Override
    public String toString() {
        return "ROSCompiledPackage{\"" + getName() + "\"}";
    }

    @NotNull
    @Override
    public PsiDirectory[] getRoots() {
        return rootMap.values().toArray(new PsiDirectory[0]);
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Nullable
    @Override
    public PsiDirectory getMsgRoot() {
        return rootMap.get(RootType.SHARE).findSubdirectory("msg");
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return ROSIcons.LIB_PKG;
    }

    @Nullable
    @Override
    public PsiDirectory getRoot(RootType type) {
        return rootMap.get(type);
    }
}
