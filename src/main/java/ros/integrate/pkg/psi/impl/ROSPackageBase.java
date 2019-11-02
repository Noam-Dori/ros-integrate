package ros.integrate.pkg.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Queryable;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.PsiElementBase;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkt.psi.ROSPktFile;
import ros.integrate.pkg.ROSPackageManager;
import ros.integrate.pkg.psi.ROSPackage;

import java.util.*;

public abstract class ROSPackageBase extends PsiElementBase implements ROSPackage, Queryable {
    private static final Logger LOG = Logger.getInstance("#ros.integrate.workspace.ROSSourcePackage");

    @NotNull
    private ROSPackageXml pkgXml;
    @NotNull
    private String name;
    @NotNull
    private final Set<ROSPktFile> packets;
    @NotNull
    private final Project project;


    ROSPackageBase(@NotNull Project project, @NotNull String name, @NotNull XmlFile pkgXml) {
        this.name = name;
        this.project = project;
        this.pkgXml = ROSPackageXml.newInstance(pkgXml,this);
        this.packets = new TreeSet<>();
    }

    private ROSPackageManager getPackageManager() {
        return project.getComponent(ROSPackageManager.class);
    }

    @Override
    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public PsiDirectory[] getDirectories() {
        return getDirectories(GlobalSearchScope.allScope(project));
    }

    @NotNull
    @Override
    public PsiDirectory[] getDirectories(@NotNull GlobalSearchScope scope) {
        return new PsiDirectory[0];
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
        // TODO change package.xml
        // TODO change CMakeLists.txt
        return this;
    }

    @NotNull
    @Override
    public Language getLanguage() {
        return Language.ANY;
    }

    @NotNull
    @Override
    public PsiElement[] getChildren() {
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
        return null; //TODO if meta-packages or workspaces are added, use those instead.
    }

    @Override
    public void putInfo(@NotNull Map<String, String> info) {
        info.put("fileName", getName());
    }

    // --- junk methods that are there because of implementation ---

    @Override
    public TextRange getTextRange() {
        return null;
    }

    @Override
    public int getStartOffsetInParent() {
        return -1;
    }

    @Override
    public int getTextLength() {
        return -1;
    }

    @Override
    public int getTextOffset() {
        return -1;
    }

    @Override
    public ASTNode getNode() {
        return null;
    }

    @Override
    public String getText() {
        return "";
    }

    @NotNull
    @Override
    public char[] textToCharArray() {
        return ArrayUtil.EMPTY_CHAR_ARRAY;
    }

    @Nullable
    @Override
    public PsiElement findElementAt(int offset) {
        return null;
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
    public void setPackets(Collection<ROSPktFile> packets) { // yes, we can retain, but we need to modify things in all packets
        removePackets(this.packets);
        addPackets(packets);
    }

    @Override
    public void removePackets(Collection<ROSPktFile> packets) {
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
