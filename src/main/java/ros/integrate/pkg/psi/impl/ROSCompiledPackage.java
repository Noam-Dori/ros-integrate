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

public class ROSCompiledPackage extends ROSPackageBase {

    @NotNull
    private final Map<RootType,PsiDirectory> rootMap;

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
        return ROSIcons.LibPkg;
    }

    @Nullable
    @Override
    public PsiDirectory getRoot(RootType type) {
        return rootMap.get(type);
    }
}
