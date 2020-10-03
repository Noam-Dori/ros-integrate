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

/**
 * represents packages that are not compiled. There packages contain source code and can be quite messy.
 * These usually exist somewhere in the workspace (under src) or in additional places specified by the user.
 * These can be organised in extra folders.
 * @author Noam Dori
 */
public class ROSSourcePackage extends ROSPackageBase {

    @NotNull
    private final PsiDirectory root;

    public ROSSourcePackage(@NotNull Project project,@NotNull String name,@NotNull PsiDirectory root,
                            @NotNull XmlFile pkgXml,@NotNull Collection<ROSPktFile> packets) {
        super(project,name,pkgXml);
        this.root = root;
        addPackets(packets);
    }

    @Override
    public String toString() {
        return "ROSSourcePackage{\"" + getName() + "\"}";
    }

    @NotNull
    @Override
    public PsiDirectory[] getRoots() {
        return new PsiDirectory[]{root};
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Nullable
    @Override
    public PsiDirectory getMsgRoot() { //TODO use CMakeLists to help
        PsiDirectory msgDir = root.findSubdirectory("msg");
        return msgDir == null ? root : msgDir;
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return ROSIcons.SRC_PKG;
    }

    @Nullable
    @Override
    public PsiDirectory getRoot(RootType type) {
        return root; // update later once include and other join in the party.
    }
}
