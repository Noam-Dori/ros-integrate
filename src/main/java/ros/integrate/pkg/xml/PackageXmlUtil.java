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
import ros.integrate.pkg.xml.ui.PackageXmlDialog;
import ros.integrate.settings.ROSSettings;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * a collection of utility functions for handling package.xml files.
 * @author Noam Dori
 */
public class PackageXmlUtil {
    private static final String PACKAGE_XML = "package.xml";

    /**
     * finds a package.xml file that was not excluded within a directory
     * @param root the directory to search. Note that this search is not recursive
     * @return null if no package.xml (that was not excluded) was found in root, 
     * otherwise the PSI file of the package.xml file.
     * 
     * @apiNote this function is a little smarter, it checks both for exclusion and for the root tag to be "package"
     */
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

    /**
     * finds ALL package.xml files in the project (that were not excluded)
     * @param project the project to search
     * @param scope a specific scope to narrow/limit the search
     * @return a list of all package.xml files that were not excluded in the form of pure PSI files
     */
    @Contract("_, _ -> !null")
    public static List<XmlFile> findPackageXmls(Project project, @NotNull GlobalSearchScope scope) {
        return FileTypeIndex.getFiles(XmlFileType.INSTANCE, scope)
                .stream().filter(xml -> xml.getName().equals(PackageXmlUtil.PACKAGE_XML))
                .filter(xml -> !ROSSettings.getInstance(project).getExcludedXmls().contains(xml.getPath()))
                .map(xml -> (XmlFile) PsiManager.getInstance(project).findFile(xml))
                .collect(Collectors.toList());
    }

    /**
     * gets the package.xml wrapper for a PSI file, if it exists
     * @param rawFile the raw PSI file to try finding a wrapper for
     * @return the package.xml data interface if the raw file is a package.xml manifest, null otherwise.
     */
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

    /**
     * gets the XML tag this element belongs to, whatever it is
     * @param element the element to find the XML tag for
     * @return null if this element does not belong to any XML tag, otherwise the PSI XML tag this element is part of
     */
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

    /**
     * check if this tag is a dependency type tag
     * @param tag the tag to check
     * @return true if this XML tag has the name of one of the possible dependency tag names, false otherwise
     */
    public static boolean isDependencyTag(@NotNull XmlTag tag) {
        return Arrays.stream(DependencyType.values()).map(DependencyType::getTagName)
                .anyMatch(name -> name.equals(tag.getName()));
    }

    /**
     * compiles a list of all valid dependency tag names for a specific format
     * @param format the package.xml format to check against. Use -1 to get all dependency names
     * @return a list containing the names of all valid dependency tags for this format.
     */
    @NotNull
    public static List<String> getDependNames(int format) {
        return Arrays.stream(DependencyType.values())
                .filter(dep -> dep.relevant(format))
                .map(DependencyType::getTagName)
                .collect(Collectors.toList());
    }

    /**
     * gets the dependency type of the XML tag
     * @param tag the XML tag to analyze
     * @return null if this is not a dependency tag, otherwise a valid dependency type with a name matching the tag name
     */
    public static DependencyType getDependencyType(XmlTag tag) {
        return Arrays.stream(DependencyType.values())
                .filter(name -> name.getTagName().equals(tag.getName())).findFirst().orElse(null);
    }

    /**
     * extracts the numerical version range applicable for this tag
     * @param tag the XML tag to check for version range attributes
     * @return a non-null version range describing applicable versions for the target package
     * (or whatever the tag is describing)
     */
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

    /**
     * gets the name of the attribute this element belongs to
     * @param element the PSI element to find the attribute name for
     * @return null if this element is not part of an XML attribute,
     * otherwise the name of the attribute this element belongs to
     */
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

    /**
     * extracts the condition from an XML tag
     * @param tag the tag to get a condition from
     * @return null if this tag does not contain a condition attribute, otherwise a ROS condition PSI element
     */
    @Nullable
    public static ROSCondition getCondition(@NotNull XmlTag tag) {
        return Optional.ofNullable(tag.getAttributeValue("condition"))
                .map(attrValue -> ROSConditionElementFactory.createCondition(tag.getProject(), attrValue))
                .orElse(null);
    }

    /**
     * a utility function that triggers {@link PackageXmlUtil#conditionEvaluatesToFalse(ROSCondition, int)} for tags
     * @param tagWithCondition the tag that potentially contains a condition attribute
     * @param format the package.xml format
     * @return true if the ROS condition attribute exists and {@link ROSCondition#evaluate()} returned false,
     * false otherwise.
     */
    public static boolean conditionEvaluatesToFalse(@NotNull Conditioned tagWithCondition, int format) {
        return conditionEvaluatesToFalse(tagWithCondition.getCondition(), format);
    }

    /**
     * checks if this condition exists and evaluates to false, which usually means that a tag should be toggled off
     * @param condition the ROS condition to evaluate. This may be null
     * @param format the package.xml format
     * @return true if the ROS condition is not null and {@link ROSCondition#evaluate()} returned false,
     * false otherwise.
     */
    public static boolean conditionEvaluatesToFalse(@Nullable ROSCondition condition, int format) {
        return format >= 3 && condition != null && condition.checkValid() && condition.evaluate().isEmpty();
    }

    /**
     * checks if the conditions of two tags can both be true at some point, which can lead to nasty interference
     * @param lhs a tag that potentially contains a condition attribute
     * @param rhs another tag that potentially contains a condition attribute
     * @param format the package.xml format
     * @return true if one of the following is true:
     * <ol>
     *     <li>the format does not permit conditions</li>
     *     <li>the conditions are identical (both null also counts)</li>
     *     <li>both conditions exist, are both valid, and both evaluate to true</li>
     * </ol>
     */
    public static boolean mayConflict(@NotNull Conditioned lhs, @NotNull Conditioned rhs, int format) {
        ROSCondition condL = lhs.getCondition(), condR = rhs.getCondition();
        return format < 3 || // conditions don't matter
                Objects.equals(condL, condR) || // takes care of null and identical conditions
                (condL != null && condR != null && condL.checkValid() && condR.checkValid()
                        && !condL.evaluate().isEmpty() && !condR.evaluate().isEmpty()); // both conditions eval to true
    }

    /**
     * Copies user collected information into the package.xml file.
     * @param dialog a completed dialog with details about the package.xml
     * @param pkgXml the package.xml to write the information into
     */
    public static void overwrite(@NotNull PackageXmlDialog dialog, @NotNull ROSPackageXml pkgXml) {
        pkgXml.setFormat(dialog.getFormat());
        pkgXml.setPkgName(dialog.getName());
//        pkgXml.setVersion(dialog.getVersion());
        pkgXml.setDescription(dialog.getDescription());
//        overwriteList(dialog.getLicenses() ,pkgXml.getLicences().size(), pkgXml::setLicense, pkgXml::addLicense);
//        overwriteList(dialog.getMaintainers() ,pkgXml.getMaintainers().size(), pkgXml::setMaintainer, pkgXml::addMaintainer);
//        overwriteList(dialog.getDependencies() ,pkgXml.getDependencies(null).size(), pkgXml::setDependency, pkgXml::addDependency);
    }

    private static <T> void overwriteList(@NotNull List<T> list, int existing, BiConsumer<Integer, T> set,
                                          Consumer<T> add) {
        int id = 0;
        for (T item : list) {
            if (id < existing) {
                set.accept(id, item);
            } else {
                add.accept(item);
            }
            id++;
        }
    }
}
