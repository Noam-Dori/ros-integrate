package ros.integrate.pkg.xml;

import com.intellij.lang.html.HTMLLanguage;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.InjectedLanguagePlaces;
import com.intellij.psi.LanguageInjector;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlText;
import org.jetbrains.annotations.NotNull;

/**
 * Injects things for the ROS package.xml
 */
public class ROSPackageXmlInjector implements LanguageInjector {
    @Override
    public void getLanguagesToInject(@NotNull PsiLanguageInjectionHost host, @NotNull InjectedLanguagePlaces injectionPlacesRegistrar) {
        if (PackageXmlUtil.getWrapper(host.getContainingFile()) == null) {
            return;
        }
        XmlTag tag = PackageXmlUtil.getParentTag(host);
        if (tag == null) {
            return;
        }
        if (host instanceof XmlText && tag.getName().equals("description")) {
            injectionPlacesRegistrar.addPlace(HTMLLanguage.INSTANCE,
                    new TextRange(0, host.getTextLength()), null, null);
        }
    }
}
