package ros.integrate.pkg.xml;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.ROSPackageManager;
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.settings.ROSSettings;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PackageXmlUtil {
    private static final String PACKAGE_XML = "package.xml";

    @Nullable
    public static XmlFile findPackageXml(@NotNull PsiDirectory root) {
        if (!root.getVirtualFile().isValid()) {
            return null;
        }
        XmlFile result = (XmlFile) root.findFile(PACKAGE_XML);

        if (result == null || ROSSettings.getInstance(root.getProject())
                .getExcludedXmls().contains(result.getVirtualFile().getPath())) {
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

    @Contract("_, _ -> !null")
    public static List<XmlFile> findPackageXmls(Project project, @NotNull GlobalSearchScope scope) {
        return FileTypeIndex.getFiles(XmlFileType.INSTANCE, scope)
                .stream().filter(xml -> xml.getName().equals(PackageXmlUtil.PACKAGE_XML))
                .filter(xml -> !ROSSettings.getInstance(project).getExcludedXmls().contains(xml.getPath()))
                .map(xml -> (XmlFile)PsiManager.getInstance(project).findFile(xml))
                .collect(Collectors.toList());
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
