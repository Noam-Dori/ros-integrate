package ros.integrate.pkg.xml;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.pkg.xml.impl.ROSPackageXmlImpl;

import java.util.List;

/**
 * A facade class that simplifies interactions with a package.xml file
 */
public interface ROSPackageXml {
    /**
     * @return the raw XML file this wrapper manages
     */
    @NotNull
    XmlFile getRawXml();

    /**
     * changes the raw xml file managed under this wrapper.
     * @param newXml the new package.xml file this wrapper manages.
     */
    void setRawXml(@NotNull XmlFile newXml);

    /**
     * binds an xml file to a packageXml file.
     * @param xmlToWrap the XML file to wrap with packageXml
     * @return a new instance of ROSPackageXml bound to the XML file.
     */
    @Contract("_,_ -> new")
    @NotNull
    static ROSPackageXml newInstance(@NotNull XmlFile xmlToWrap, @NotNull ROSPackage pkg) {
        return new ROSPackageXmlImpl(xmlToWrap, pkg);
    }

    int getFormat();

    @NotNull
    TextRange getFormatTextRange();

    ROSPackage getPackage();

    String getPkgName();

    TextRange getRootTextRange();

    TextRange getNameTextRange();

    void setNewFormat();

    void setPkgName(String name);

    @Nullable
    String getVersion();

    @NotNull
    TextRange getVersionTextRange();

    void setVersion(String newVersion);

    String getDescription();

    void setDescription(String newDescription);

    TextRange getDescriptionTextRange();

    @NotNull
    List<String> getLicences();

    @NotNull
    List<String> getURLs();

    /**
     * @return a list with at least length 1 that points towards all text ranges of the license values,
     *         or if no license is available, to the package tag.
     */
    @NotNull
    List<TextRange> getLicenceTextRanges();

    /**
     * @return a list with at least length 1 that points towards all text ranges of the license values,
     *         or if no license is available, to the package tag.
     */
    @NotNull
    List<TextRange> getURLTextRanges();

    void addLicence(String newLicence);
}
