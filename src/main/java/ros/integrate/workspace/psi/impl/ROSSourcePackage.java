package ros.integrate.workspace.psi.impl;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.psi.ROSPktFile;

import java.util.List;

public class ROSSourcePackage extends ROSPackageBase {
    private static final Logger LOG = Logger.getInstance("#ros.integrate.workspace.ROSProjectPackageFinder");

    @NotNull
    private PsiDirectory root;
    @NotNull
    private List<ROSPktFile> packets;

    public ROSSourcePackage(@NotNull Project project,@NotNull String name,@NotNull PsiDirectory root,
                            @NotNull XmlFile pkgXml,@NotNull List<ROSPktFile> packets) {
        super(project,name,pkgXml);
        this.root = root;
        this.packets = packets;
        packets.forEach(pkt -> pkt.setPackage(this));
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
                .filter(pkt -> pkt.getName().equals(pktName)).toArray(ROSPktFile[]::new);
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
}
