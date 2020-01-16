package ros.integrate.pkg.xml;

import com.intellij.openapi.paths.WebReference;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class PackageXmlReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(XmlToken.class), new PsiReferenceProvider() {
            @NotNull
            @Override
            public PsiReference[] getReferencesByElement(@NotNull PsiElement element,
                                                         @NotNull ProcessingContext context) {
                if (PackageXmlUtil.getWrapper(element.getContainingFile()) == null) {
                    return PsiReference.EMPTY_ARRAY;
                }
                XmlTag parentTag = PackageXmlUtil.getParentTag(element);
                if (parentTag.getName().equals("license")) {
                    String url = ROSLicenses.AVAILABLE_LICENSES.get(parentTag.getValue().getText());
                    return url == null || url.isEmpty() ? PsiReference.EMPTY_ARRAY :
                            new PsiReference[]{new WebReference(element, element.getParent().getTextRange()
                                    .shiftLeft(element.getTextOffset()), url)};
                }
                else if (parentTag.getName().equals("url")) {
                    String url = parentTag.getValue().getText();
                    return url.isEmpty() ? PsiReference.EMPTY_ARRAY :
                            new PsiReference[]{new WebReference(element, element.getParent().getTextRange()
                                    .shiftLeft(element.getTextOffset()), url)};
                }
                return PsiReference.EMPTY_ARRAY;
            }
        });
    }
}
