package ros.integrate.pkg.xml;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.paths.WebReference;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.UnfairTextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;

public class PackageXmlReferenceContributor extends PsiReferenceContributor {
    private static class LicenseReference extends WebReference {
        LicenseReference(@NotNull PsiElement element, TextRange textRange, @Nullable String url) {
            super(element, textRange, url);
        }

        @NotNull
        @Override
        public Object[] getVariants() { // there is a bug where this method is not called.
            return ROSLicenses.AVAILABLE_LICENSES.keySet().stream().map(LookupElementBuilder::create).toArray();
        }
    }

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
                if (parentTag == null) {
                    return PsiReference.EMPTY_ARRAY;
                }
                if (parentTag.getName().equals("license")) {
                    String url = ROSLicenses.AVAILABLE_LICENSES.get(parentTag.getValue().getText());
                    return url == null || url.isEmpty() ? PsiReference.EMPTY_ARRAY :
                            new PsiReference[]{new LicenseReference(element, getUnfairTr(element), url)};
                } else if (parentTag.getName().equals("url")) {
                    String url = parentTag.getValue().getText();
                    try {
                        new URL(url);
                        return url.isEmpty() ? PsiReference.EMPTY_ARRAY :
                                new PsiReference[]{new WebReference(element, element.getParent().getTextRange()
                                        .shiftLeft(element.getTextOffset()), url)};
                    } catch (MalformedURLException ignored) {
                    }
                }
                return PsiReference.EMPTY_ARRAY;
            }
        });
    }

    @NotNull
    @Contract("_ -> new")
    private static TextRange getUnfairTr(@NotNull PsiElement element) {
        TextRange elementTr = element.getParent().getTextRange();
        return new UnfairTextRange(elementTr.getStartOffset() - element.getTextOffset(),
                elementTr.getEndOffset() - element.getTextOffset());
    }
}
