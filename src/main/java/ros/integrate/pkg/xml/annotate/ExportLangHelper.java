package ros.integrate.pkg.xml.annotate;

import com.intellij.openapi.extensions.ExtensionPointName;
import ros.integrate.pkg.psi.ROSPackage;

public interface ExportLangHelper {
    ExtensionPointName<ExportLangHelper> EP_NAME = ExtensionPointName.create("ros-integrate.exportLangHelper");

    /**
     * @param lang the language the package is supposed to generate messages for.
     * @param aPackage the package that is checked for a message generator.
     * @return true if aPackage generates message code for language lang.
     */
    boolean messageGeneratorFor(String lang, ROSPackage aPackage);
}
