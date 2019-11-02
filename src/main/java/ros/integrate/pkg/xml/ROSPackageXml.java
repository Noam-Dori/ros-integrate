package ros.integrate.pkg.xml;

import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.impl.ROSPackageXmlImpl;

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
    @Contract("_ -> new")
    @NotNull
    static ROSPackageXml newInstance(@NotNull XmlFile xmlToWrap) {
        return new ROSPackageXmlImpl(xmlToWrap);
    }
}
