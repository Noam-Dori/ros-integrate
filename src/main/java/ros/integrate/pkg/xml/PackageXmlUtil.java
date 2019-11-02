package ros.integrate.pkg.xml;

import com.intellij.psi.PsiDirectory;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.ROSPackageManager;
import ros.integrate.pkg.psi.ROSPackage;

import java.util.Optional;

public class PackageXmlUtil {
    public static final String PACKAGE_XML = "package.xml";

    @Nullable
    public static XmlFile findPackageXml(@NotNull PsiDirectory root) {
        if (!root.getVirtualFile().isValid()) {
            return null;
        }
        XmlFile result = (XmlFile) root.findFile(PACKAGE_XML);

        if (result == null) {
            return null;
        }

        String rootTag = Optional.of(result)
                .map(XmlFile::getRootTag)
                .map(XmlTag::getName)
                .get();

        if (rootTag.isEmpty() || "package".startsWith(rootTag)) {
            return result;
        }

        return null;
    }

    @Nullable
    public static ROSPackageXml getWrapper(@NotNull XmlFile rawXml) {
        ROSPackageManager manager = rawXml.getProject().getComponent(ROSPackageManager.class);
        for (ROSPackage pkg : manager.getAllPackages()) {
            if (pkg.getPackageXml() != null && rawXml.equals(pkg.getPackageXml().getRawXml())) {
                return pkg.getPackageXml();
            }
        }
        return null;
    }
}
