package ros.integrate.pkg.psi.impl;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Queryable;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.PsiElementBase;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.cmake.adapter.CMakeFileAdapter;
import ros.integrate.pkg.ROSPackageManager;
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkt.psi.ROSPktFile;

import java.util.*;

/**
 * a base implementation for ROS packages. You can extend this class to make
 * implementing a new package type a lot easier.
 */
public abstract class ROSPackageBase extends PsiElementBase implements ROSPackage, Queryable {
    private static final Logger LOG = Logger.getInstance("#ros.integrate.workspace.ROSPackageBase");

    @NotNull
    private final ROSPackageXml pkgXml;
    @NotNull
    private String name;
    @NotNull
    private final Set<ROSPktFile> packets;
    @NotNull
    private final Project project;

    /**
     * construct a new package (this class is abstract however)
     * @param project the project this package belongs to
     * @param name the name of the package. This is the fully qualifies name as well, used for indexing.
     * @param pkgXml the reference package.xml file
     */
    ROSPackageBase(@NotNull Project project, @NotNull String name, @NotNull XmlFile pkgXml) {
        this.name = name;
        this.project = project;
        this.pkgXml = ROSPackageXml.newInstance(pkgXml,this);
        this.packets = new TreeSet<>();
    }

    private ROSPackageManager getPackageManager() {
        return project.getService(ROSPackageManager.class);
    }

    @Override
    @NotNull
    public String getName() {
        return name;
    }

    @Override
    public void checkSetName(String name) throws IncorrectOperationException {
        if(getPackageManager().findPackage(name) != null) {
            throw new IncorrectOperationException("A package named \"" +
                    name + "\" already exists! remove or updatePackageName it first.");
        }
    }


    @Override
    public ROSPackageBase setName(@NotNull String name) throws IncorrectOperationException {
        checkSetName(name);
        getPackageManager().updatePackageName(this, name);
        this.name = name;
        Arrays.stream(getRoots()).filter(root -> !root.getName().equals(name)).forEach(root -> root.setName(name));
        // TODO change CMakeLists.txt
        return this;
    }

    @Override
    public PsiElement @NotNull [] getChildren() {
        // concrete packages don't have child packages, but they do contain things.
        ArrayList<PsiElement> ret = new ArrayList<>();
        Arrays.stream(getRoots()).forEach(root -> {
            ret.addAll(Arrays.asList(root.getFiles()));
            ret.addAll(Arrays.asList(root.getSubdirectories()));
        });
        return ret.toArray(new PsiElement[0]);
    }

    @Override
    public PsiElement getParent() {
        return null;
    }

    @Override
    public void putInfo(@NotNull Map<? super String, ? super String> info) {
        info.put("fileName", getName());
    }

    @Override
    public boolean isValid() {
        return !project.isDisposed()
                && getPackageManager().findPackage(name) != null
                && getRoots().length > 0;
    }

    @Override
    public PsiFile getContainingFile() {
        return null;
    }

    @NotNull
    @Override
    public PsiElement getNavigationElement() {
        return Objects.requireNonNull(getRoot(RootType.SHARE)); // packages MUST have a share root, as it contains the package.xml stuff
    }

    @Nullable
    @Override
    abstract public PsiDirectory getRoot(RootType type);

    @Override
    @NotNull
    public Project getProject() {
        return project;
    }

    @Override
    public void addPackets(Collection<ROSPktFile> packets) {
        this.packets.addAll(packets);
        packets.forEach(pkt -> pkt.setPackage(this));
    }

    @Override
    public void setPackets(@NotNull Collection<ROSPktFile> packets) { // yes, we can retain, but we need to modify things in all packets
        removePackets(this.packets);
        addPackets(packets);
    }

    @Override
    public void removePackets(@NotNull Collection<ROSPktFile> packets) {
        packets.forEach(pkt -> pkt.setPackage(ORPHAN));
        this.packets.removeAll(packets);
    }

    @NotNull
    @Override
    public ROSPktFile[] getPackets(@NotNull GlobalSearchScope scope) {
        return packets.parallelStream()
                .filter(pkt -> scope.accept(pkt.getVirtualFile())).toArray(ROSPktFile[]::new);
    }

    @Nullable
    @Override
    public <T extends ROSPktFile> T findPacket(@NotNull String pktName, @NotNull Class<T> pktType) {
        ROSPktFile[] validFiles = packets.parallelStream()
                .filter(pkt -> pkt.getPacketName().equals(pktName)).toArray(ROSPktFile[]::new);
        if(validFiles.length > 1) {
            LOG.error("Found 2 messages with the same name in the same package.",
                    "Package name: " + name,
                    "Packet file name: " + pktName);
        }
        return validFiles.length == 1 && pktType.isInstance(validFiles[0]) ? pktType.cast(validFiles[0]) : null;
    }

    @NotNull
    @Override
    public ROSPackageXml getPackageXml() {
        return pkgXml;
    }

    @Override
    public void setPackageXml(XmlFile newPackageXml) {
        pkgXml.setRawXml(newPackageXml);
    }
}
