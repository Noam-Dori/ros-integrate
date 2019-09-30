package ros.integrate.workspace.psi.impl;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.containers.hash.HashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.ROSIcons;
import ros.integrate.pkt.psi.ROSPktFile;

import javax.swing.*;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ROSCompiledPackage extends ROSPackageBase {
    private static final Logger LOG = Logger.getInstance("#ros.integrate.workspace.ROSCompiledPackage");

    public enum RootType {
        SHARE
    }

    @NotNull
    private Map<RootType,PsiDirectory> rootMap;
    @NotNull
    private Set<ROSPktFile> packets;

    public ROSCompiledPackage(@NotNull Project project, @NotNull String name, @NotNull Map<RootType, PsiDirectory> rootMap,
                            @NotNull XmlFile pkgXml, @NotNull Collection<ROSPktFile> packets) {
        super(project,name,pkgXml);
        this.rootMap = rootMap;
        this.packets = new HashSet<>();
        addPackets(packets);
    }

    @Override
    public String toString() {
        return "ROSCompiledPackage{\"" + getName() + "\"}";
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
        return rootMap.values().toArray(new PsiDirectory[0]);
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Nullable
    @Override
    public PsiDirectory getMsgRoot() {
        return rootMap.get(RootType.SHARE).findSubdirectory("msg");
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

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return ROSIcons.LibPkg;
    }
}
