package ros.integrate.pkg.psi.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.PsiElementBase;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.ROSIcons;
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkt.psi.ROSPktFile;

import javax.swing.*;
import java.util.Collection;

/**
 * a rosdep key. This is not a real ROS package, but it can be depended on from package.xml dependency tags. Things like
 * the cmake executable belong here. The reason they are together with ROSPackage
 * is so that they can be put together in the same list.
 *
 * @apiNote objects from this class have very limited functionality. Here are the functions you can use:
 * <ul>
 *     <li>{@link ROSDepKey#getName()}</li>
 *     <li>{@link ROSDepKey#getProject()}</li>
 *     <li>{@link ROSDepKey#getIcon(int)}</li>
 *     <li>{@link ROSDepKey#isValid()}</li>
 *     <li>{@link ROSDepKey#isEditable()}</li>
 * </ul>
 * The rest work, but won't do much good or outright ignore the request.
 * @author Noam Dori
 */
public class ROSDepKey extends PsiElementBase implements ROSPackage {
    @NotNull
    private final String name;
    @NotNull
    private final Project project;

    /**
     * construct a new dependency key
     * @param project the project this key belongs to
     * @param name the key.
     */
    public ROSDepKey(@NotNull Project project, @NotNull String name) {
        this.name = name;
        this.project = project;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public Project getProject() {
        return project;
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return ROSIcons.DEP_KEY;
    }

    @Override
    public boolean isValid() {
        return !project.isDisposed();
    }

    @NotNull
    @Override
    public PsiElement[] getChildren() {
        return new PsiElement[0];
    }

    @Nullable
    @Contract(pure = true)
    @Override
    public PsiFile getContainingFile() {
        return null;
    }

    @Override
    public void checkSetName(String name) throws IncorrectOperationException {
        throw new IncorrectOperationException("rosdep keys may not be renamed.");
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        throw new IncorrectOperationException("rosdep keys may not be renamed.");
    }

    @NotNull
    @Override
    public ROSPktFile[] getPackets(@NotNull GlobalSearchScope scope) {
        return new ROSPktFile[0];
    }

    @Nullable
    @Override
    public <T extends ROSPktFile> T findPacket(@NotNull String pktName, @NotNull Class<T> pktType) {
        return null;
    }

    @NotNull
    @Override
    public PsiDirectory[] getRoots() {
        return new PsiDirectory[0];
    }

    @Nullable
    @Override
    public PsiDirectory getRoot(RootType type) {
        return null;
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Nullable
    @Override
    public PsiDirectory getMsgRoot() {
        return null;
    }

    @Nullable
    @Override
    public ROSPackageXml getPackageXml() {
        return null;
    }

    @Override
    public void addPackets(Collection<ROSPktFile> packets) {}

    @Override
    public void setPackets(Collection<ROSPktFile> packets) {}

    @Override
    public void removePackets(Collection<ROSPktFile> packets) {}

    @Override
    public void setPackageXml(XmlFile newPackageXml) {}

    @Override
    public String toString() {
        return "ROSDepKey{\"" + name + "\"}";
    }
}
