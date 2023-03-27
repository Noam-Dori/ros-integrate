package ros.integrate.cmake.ref;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.ref.ROSPackageReferenceBase;

/**
 * links the CMakeLists.txt project name to the package name, which should be identical per ROS1 and ROS2 standards:
 * <a href="http://wiki.ros.org/catkin/CMakeLists.txt#Package_name">...</a>
 * <a href="https://docs.ros.org/en/foxy/How-To-Guides/Ament-CMake-Documentation.html#basic-project-outline">...</a>
 */
public class ROSCMakeToPackageReference extends ROSPackageReferenceBase<PsiElement> {
    /**
     * construct a new reference
     *
     * @param element the referencing element.
     */
    public ROSCMakeToPackageReference(@NotNull PsiElement element) {
        super(element, new TextRange(0, element.getTextLength()));
        pkgName = element.getText();
    }
}