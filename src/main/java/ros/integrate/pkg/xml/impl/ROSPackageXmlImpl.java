package ros.integrate.pkg.xml.impl;

import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ROSPackageXml;

public class ROSPackageXmlImpl implements ROSPackageXml {
    private XmlFile file;

    @Contract(pure = true)
    public ROSPackageXmlImpl(@NotNull XmlFile xmlToWrap) {
        file = xmlToWrap;
    }

    @NotNull
    @Override
    public XmlFile getRawXml() {
        return file;
    }

    @Override
    public void setRawXml(@NotNull XmlFile newXml) {
        file = newXml;
    }
}
