package ros.integrate.pkg.xml;

import com.intellij.openapi.paths.PathReferenceManager;
import com.intellij.openapi.paths.WebReference;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ref.DependencyToPackageReference;
import ros.integrate.pkg.xml.ref.NameXmlToPackageReference;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

public class PackageXmlReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(XmlTag.class), new PsiReferenceProvider() {
            @NotNull
            @Override
            public PsiReference[] getReferencesByElement(@NotNull PsiElement element,
                                                         @NotNull ProcessingContext context) {
                ROSPackageXml pkgXml = PackageXmlUtil.getWrapper(element.getContainingFile());
                if (pkgXml == null) {
                    return PsiReference.EMPTY_ARRAY;
                }
                String name = ((XmlTag)element).getName(), value = ((XmlTag)element).getValue().getText();
                if (name.equals("license")) {
                    String url = ROSLicenses.AVAILABLE_LICENSES.get(value);
                    return url == null || url.isEmpty() ? PsiReference.EMPTY_ARRAY : getWebReference(element, url);
                } else if (name.equals("url")) {
                    try {
                        new URL(value);
                        return value.isEmpty() ? PsiReference.EMPTY_ARRAY : getWebReference(element, value);
                    } catch (MalformedURLException ignored) {
                    }
                } else if (PackageXmlUtil.isDependencyTag((XmlTag) element)) {
                    return new PsiReference[]{new DependencyToPackageReference((XmlTag) element)};
                } else if (name.equals("name")) {
                    return new PsiReference[]{new NameXmlToPackageReference((XmlTag) element, pkgXml)};
                }
                return PsiReference.EMPTY_ARRAY;
            }
        });
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(XmlAttributeValue.class), new PsiReferenceProvider() {
            @NotNull
            @Override
            public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                if (PackageXmlUtil.getWrapper(element.getContainingFile().getOriginalFile()) == null) {
                    return PsiReference.EMPTY_ARRAY;
                }
                if (Optional.ofNullable(PackageXmlUtil.getParentTag(element)).map(XmlTag::getName)
                        .orElse("").equals("license")) {
                    return PathReferenceManager.getInstance().createReferences(element, false);
                }
                return PsiReference.EMPTY_ARRAY;
            }
        });
    }

    @NotNull
    @Contract("_,_ -> new")
    private static PsiReference[] getWebReference(@NotNull PsiElement element, @NotNull String url) {
        TextRange valueTr = ((XmlTag)element).getValue().getTextRange();
        return new PsiReference[]{new WebReference(element, valueTr.shiftLeft(element.getTextOffset()), url)};
    }
}
