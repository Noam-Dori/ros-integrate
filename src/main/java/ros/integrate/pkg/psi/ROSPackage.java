package ros.integrate.pkg.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiElementBase;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkt.psi.ROSPktFile;

import javax.swing.*;
import java.util.Collection;

/**
 * represents a low-level ROS package. it does not contain any packages. That's the job of the meta-package.
 * ROS packages must have two things:
 * 1. a package.xml file
 * 2. a CMakeLists.txt file which has the {@code catkin_package()} function
 */
public interface ROSPackage extends PsiCheckedRenameElement, NavigatablePsiElement, Comparable<ROSPackage> {
    enum RootType {
        SHARE
    }

    /**
     * a default ROS package everything is referenced to if a ROS file does not belong to any package.
     */
    ROSPackage ORPHAN = new ROSOrphanPackage();

    /**
     * @return the name of the element. Should never be empty.
     */
    @Override
    @NotNull
    String getName();

    @Override
    Icon getIcon(int flags);

    /**
     * Returns the list of packet files in directories corresponding to the package in the specified
     * search scope.
     *
     * @param scope the scope in which directories are searched.
     * @return the array of packet files.
     * for sources, they can be found anywhere within the root dir and below.
     * Use CMakeLists.txt for help where to search.
     * For built packages, they are all placed in specialised directories (msg and srv) within the share directory
     */
    @NotNull
    ROSPktFile[] getPackets(@NotNull GlobalSearchScope scope);

    /**
     * finds a specific packet in the package
     *
     * @param pktName the name of the packet file to search for.
     * @param <T>     allows filtering by type of file. If you don't wish to use this filter, set this to <code>ROSPktFile.class</code>
     * @return null if the packet if not found, otherwise the packet file you are searching for.
     */
    @Nullable
    <T extends ROSPktFile> T findPacket(@NotNull String pktName, @NotNull Class<T> pktType);

    /**
     * @return the roots of this package.
     */
    @NotNull
    PsiDirectory[] getRoots();

    @Nullable
    PsiDirectory getRoot(RootType type);

    /**
     * @return true if this package is supposed to be edited, false otherwise.
     */
    boolean isEditable();

    /**
     * @return gets the recommended message file root.
     */
    @Nullable
    PsiDirectory getMsgRoot();

    /**
     * @return the package.xml file.
     *         for source packages, this is simply in the root of the source folder.
     *         for built packages, this is in their root folder in the "share" directory.
     */
    @Nullable
    ROSPackageXml getPackageXml();

    /**
     * add packets to this package.
     * @param packets the collection of packages to add
     */
    void addPackets(Collection<ROSPktFile> packets);

    /**
     * add packets to this package and makes sure that these are the ONLY packets in the package.
     * @param packets the collection of packages to add
     */
    void setPackets(Collection<ROSPktFile> packets);

    /**
     * removes packets from this package.
     * @param packets the collection of packages to add
     */
    void removePackets(Collection<ROSPktFile> packets);

    /**
     * change this package's package.xml to this file
     * @param newPackageXml the new package.xml file
     */
    void setPackageXml(XmlFile newPackageXml);

    @Override
    default int compareTo(@NotNull ROSPackage o) {
        return getName().compareTo(o.getName());
    }

    //    /**
//     * get all source files available for this package, compiled or source.
//     * @param scope where to search if at all.
//     * @return an array of PSI source files, usually C++ or python files.
//     *         these can also be generated source code like message sources.
//     *         for sources, they can be found anywhere within the root dir and below.
//     *         Use CMakeLists.txt for help where to search.
//     *         For built packages, they are all placed in their root directory within the include directory
//     */
//    @NotNull
//    PsiFile[] getSources(@NotNull GlobalSearchScope scope);

    /*@NotNull
    ROSBundleFile[] getBundles(@NotNull GlobalSearchScope scope);*/

//    /**
//     * @return {@code null} if compiled package, the CMakeLists.txt file otherwise.
//     *         for source directories, this is simply in the root of the source folder.
//     */
//    @Nullable
//    PsiFile getCMakeLists();

//    /**
//     * Returns the list of all files in the package, restricted by the specified scope. (This is
//     * normally the list of all files in all directories corresponding to the package, but it can
//     * be modified by custom language plugins which have a different notion of packages.)
//     */
//    @NotNull
//    PsiFile[] getFiles(@NotNull GlobalSearchScope scope);

    // implementation specific methods that are default due to this being a package.

    @Override
    default TextRange getTextRange() {
        return null;
    }

    @Override
    default int getStartOffsetInParent() {
        return -1;
    }

    @Override
    default int getTextLength() {
        return -1;
    }

    @Override
    default int getTextOffset() {
        return -1;
    }

    @Override
    default ASTNode getNode() {
        return null;
    }

    @Override
    default String getText() {
        return "";
    }

    @NotNull
    @Override
    default char[] textToCharArray() {
        return ArrayUtil.EMPTY_CHAR_ARRAY;
    }

    @Nullable
    @Override
    default PsiElement findElementAt(int offset) {
        return null;
    }

    @NotNull
    @Override
    default Language getLanguage() {
        return Language.ANY;
    }

    @Nullable
    @Contract(pure = true)
    @Override
    default PsiElement getParent() {
        return null;
    }


    final class ROSOrphanPackage extends PsiElementBase implements ROSPackage {
        private ROSOrphanPackage() {}

        @NotNull
        @Contract(pure = true)
        @Override
        public String toString() {
            return "ROSOrphanPackage{\"\"}";
        }

        @Contract(pure = true)
        @NotNull
        @Override
        public String getName() {
            return "";
        }

        @Contract(value = "_ -> new", pure = true)
        @NotNull
        @Override
        public ROSPktFile[] getPackets(@NotNull GlobalSearchScope scope) {
            return new ROSPktFile[0];
        }

        @Contract(pure = true)
        @Nullable
        @Override
        public <T extends ROSPktFile> T findPacket(@NotNull String pktName, @NotNull Class<T> pktType) {
            return null;
        }

        @Contract(pure = true)
        @Override
        @NotNull
        public PsiDirectory[] getRoots() {
            return PsiDirectory.EMPTY_ARRAY;
        }

        @Contract(pure = true)
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
        public void setPackageXml(XmlFile newPackageXml) { }

        @Contract("_ -> fail")
        @Override
        public void checkSetName(String name) {
            throw new IncorrectOperationException("The Orphan package cannot be renamed.");
        }

        @Contract("_ -> fail")
        @Override
        public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
            throw new IncorrectOperationException("The Orphan package cannot be renamed.");
        }

        @Contract(pure = true)
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

    }
}
