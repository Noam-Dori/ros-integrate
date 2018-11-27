package ros.integrate.workspace.psi;

import com.intellij.navigation.NavigationItem;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.psi.ROSPktFile;

/**
 * represents a low-level ROS package. it does not contain any packages. That's the job of the meta-package.
 * ROS packages must have two things:
 * 1. a package.xml file
 * 2. a CMakeLists.txt file which has the {@code catkin_package()} function
 */
public interface ROSPackage extends PsiCheckedRenameElement, NavigationItem, PsiModifierListOwner,
        PsiDirectoryContainer, PsiQualifiedNamedElement {
    /**
     * Returns the full-qualified name of the package.
     * Full-qualified name includes location from workspace
     *
     * @return the full-qualified name. Should never be empty
     */
    @Override
    @NotNull
    String getQualifiedName();

    /**
     * @return the name of the element. Should never be empty.
     */
    @Override
    @NotNull
    String getName();

    /**
     * Returns the list of packet files in directories corresponding to the package in the specified
     * search scope.
     *
     * @param scope the scope in which directories are searched.
     * @return the array of packet files.
     *         for sources, they can be found anywhere within the root dir and below.
     *         Use CMakeLists.txt for help where to search.
     *         For built packages, they are all placed in specialised directories (msg and srv) within the share directory
     */
    @NotNull
    ROSPktFile[] getPackets(@NotNull GlobalSearchScope scope);

    /**
     * get all source files available for this package, compiled or source.
     * @param scope where to search if at all.
     * @return an array of PSI source files, usually C++ or python files.
     *         these can also be generated source code like message sources.
     *         for sources, they can be found anywhere within the root dir and below.
     *         Use CMakeLists.txt for help where to search.
     *         For built packages, they are all placed in their root directory within the include directory
     */
    @NotNull
    PsiFile[] getSources(@NotNull GlobalSearchScope scope);

    /*@NotNull
    ROSBundleFile[] getBundles(@NotNull GlobalSearchScope scope);*/

    /**
     * @return {@code null} if compiled package, the CMakeLists.txt file otherwise.
     *         for source directories, this is simply in the root of the source folder.
     */
    @Nullable
    PsiFile getCMakeLists();

    /**
     * @return the package.xml file.
     *         for source packages, this is simply in the root of the source folder.
     *         for built packages, this is in their root folder in the "share" directory.
     */
    @NotNull
    XmlFile getPackageXml();

//    /**
//     * @return all the packages this specific package requires to work.
//     *         An empty array means this package depends on no-one.
//     */
//    @NotNull
//    ROSPackage[] getDependencies();

    /**
     * Returns the list of all files in the package, restricted by the specified scope. (This is
     * normally the list of all files in all directories corresponding to the package, but it can
     * be modified by custom language plugins which have a different notion of packages.)
     */
    @NotNull
    PsiFile[] getFiles(@NotNull GlobalSearchScope scope);
}
