package ros.integrate.workspace.psi.impl;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.containers.hash.HashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.psi.ROSPktFile;

import java.util.Collection;
import java.util.Set;

public class ROSSourcePackage extends ROSPackageBase {
    private static final Logger LOG = Logger.getInstance("#ros.integrate.workspace.ROSProjectPackageFinder");

    @NotNull
    private PsiDirectory root;
    @NotNull
    private Set<ROSPktFile> packets;

    public ROSSourcePackage(@NotNull Project project,@NotNull String name,@NotNull PsiDirectory root,
                            @NotNull XmlFile pkgXml,@NotNull Collection<ROSPktFile> packets) {
        super(project,name,pkgXml);
        this.root = root;
        this.packets = new HashSet<>();
        addPackets(packets);
    }

    @Override
    public String toString() {
        return "ROSSourcePackage{\"" + getName() + "\"}";
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

    @NotNull
    @Override
    public XmlFile getPackageXml() {
        return pkgXml;
    }

    @Override
    public void addPackets(Collection<ROSPktFile> packets) {
        this.packets.addAll(packets);
        packets.forEach(pkt -> pkt.setPackage(this));
    }

    @Override
    public void setPackets(Collection<ROSPktFile> packets) {
        removePackets(this.packets);
        addPackets(packets);
    }

    @Override
    public void removePackets(Collection<ROSPktFile> packets) {
        packets.forEach(pkt -> pkt.setPackage(ORPHAN));
        this.packets.removeAll(packets);
    }

    @Override
    public void setPackageXml(XmlFile newPackageXml) {
        pkgXml = newPackageXml;
    }
}
