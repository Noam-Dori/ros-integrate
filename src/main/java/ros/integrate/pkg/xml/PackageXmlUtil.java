package ros.integrate.pkg.xml;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.ROSPackageManager;
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.pkg.xml.condition.psi.ROSCondition;
import ros.integrate.pkg.xml.condition.psi.ROSCondition.Conditioned;
import ros.integrate.pkg.xml.condition.psi.ROSConditionElementFactory;
import ros.integrate.settings.ROSSettings;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
                .orElse("");

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
                .map(xml -> (XmlFile) PsiManager.getInstance(project).findFile(xml))
                .collect(Collectors.toList());
    }

    @Nullable
    public static ROSPackageXml getWrapper(@NotNull PsiFile rawFile) {
        ROSPackageManager manager = rawFile.getProject().getService(ROSPackageManager.class);
        for (ROSPackage pkg : manager.getAllPackages()) {
            if (pkg.getPackageXml() != null && rawFile.equals(pkg.getPackageXml().getRawXml())) {
                return pkg.getPackageXml();
            }
        }
        return null;
    }

    @Nullable
    public static XmlTag getParentTag(@NotNull PsiElement element) {
        element = element.getParent();
        for (int i = 0; i < 3; i++, element = element.getParent()) {
            if (element instanceof XmlTag) {
                return (XmlTag) element;
            }
        }
        return null;
    }

    public static boolean isDependencyTag(@NotNull XmlTag tag) {
        return Arrays.stream(DependencyType.values()).map(DependencyType::getTagName)
                .anyMatch(name -> name.equals(tag.getName()));
    }

    @NotNull
    public static List<String> getDependNames(int format) {
        return Arrays.stream(DependencyType.values())
                .filter(dep -> dep.relevant(format))
                .map(DependencyType::getTagName)
                .collect(Collectors.toList());
    }

    public static DependencyType getDependencyType(XmlTag tag) {
        return Arrays.stream(DependencyType.values())
                .filter(name -> name.getTagName().equals(tag.getName())).findFirst().orElse(null);
    }

    @NotNull
    @Contract("_ -> new")
    public static VersionRange getVersionRange(@NotNull XmlTag tag) {
        String attrValue = tag.getAttributeValue("version_eq");
        if (attrValue != null) {
            return VersionRange.exactVersion(attrValue);
        }
        VersionRange.Builder builder = new VersionRange.Builder();
        attrValue = tag.getAttributeValue("version_lt");
        if (attrValue != null) {
            builder.max(attrValue, true);
        }
        attrValue = tag.getAttributeValue("version_lte");
        if (attrValue != null) {
            builder.max(attrValue, false);
        }
        attrValue = tag.getAttributeValue("version_gt");
        if (attrValue != null) {
            builder.min(attrValue, true);
        }
        attrValue = tag.getAttributeValue("version_gte");
        if (attrValue != null) {
            builder.min(attrValue, false);
        }
        return builder.build();
    }

    @Nullable
    public static String getAttributeName(PsiElement element) {
        element = element.getParent();
        for (int i = 0; i < 3; i++, element = element.getParent()) {
            if (element instanceof XmlAttribute) {
                return ((XmlAttribute) element).getName();
            }
        }
        return null;
    }

    @Nullable
    public static ROSCondition getCondition(@NotNull XmlTag tag) {
        return Optional.ofNullable(tag.getAttributeValue("condition"))
                .map(attrValue -> ROSConditionElementFactory.createCondition(tag.getProject(), attrValue))
                .orElse(null);
    }

    public static boolean conditionEvaluatesToFalse(@NotNull Conditioned tagWithCondition, int format) {
        return conditionEvaluatesToFalse(tagWithCondition.getCondition(), format);
    }

    public static boolean conditionEvaluatesToFalse(@Nullable ROSCondition condition, int format) {
        return format >= 3 && condition != null && condition.checkValid() && condition.evaluate().isEmpty();
    }

    public static boolean mayConflict(@NotNull Conditioned lhs, @NotNull Conditioned rhs, int format) {
        ROSCondition condL = lhs.getCondition(), condR = rhs.getCondition();
        return format < 3 || // conditions don't matter
                Objects.equals(condL, condR) || // takes care of null and identical conditions
                (condL != null && condR != null && condL.checkValid() && condR.checkValid()
                        && !condL.evaluate().isEmpty() && !condR.evaluate().isEmpty()); // both conditions eval to true
    }
}
