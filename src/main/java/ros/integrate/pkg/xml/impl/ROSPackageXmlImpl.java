package ros.integrate.pkg.xml.impl;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.pkg.xml.ROSPackageXml;

public class ROSPackageXmlImpl implements ROSPackageXml {
    private XmlFile file;
    private ROSPackage pkg;

    @Contract(pure = true)
    public ROSPackageXmlImpl(@NotNull XmlFile xmlToWrap, @NotNull ROSPackage pkg) {
        file = xmlToWrap;
        this.pkg = pkg;
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

    @Override
    public int getFormat() {
        if (file.getRootTag() == null) {
            return 0;
        }
        XmlAttribute format = file.getRootTag().getAttribute("format");
        if (format == null || format.getValue() == null) {
            return 1;
        }
        try {
            return Integer.parseInt(format.getValue());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @NotNull
    @Override
    public TextRange getFormatTextRange() {
        if (file.getRootTag() == null) {
            return file.getTextRange();
        }
        XmlAttribute format = file.getRootTag().getAttribute("format");
        if (format == null || format.getValueElement() == null) {
            return getRootTextRange();
        }
        return format.getValueElement().getValueTextRange();
    }

    @Override
    public ROSPackage getPackage() {
        return pkg;
    }

    @Override
    public String getPkgName() {
        if (file.getRootTag() == null) {
            return null;
        }
        return file.getRootTag().getSubTagText("name");
    }

    @Override
    public TextRange getRootTextRange() {
        if (file.getRootTag() == null) {
            return file.getTextRange();
        }
        return new TextRange(file.getRootTag().getTextOffset() + 1, file.getRootTag().getTextOffset() + 1 +
                file.getRootTag().getName().length());
    }

    @Override
    public TextRange getNameTextRange() {
        if (file.getRootTag() == null) {
            return file.getTextRange();
        }
        XmlTag name = file.getRootTag().findFirstSubTag("name");
        if (name == null) {
            return getRootTextRange();
        }
        return name.getValue().getTextRange();
    }
}
