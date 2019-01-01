package ros.integrate.workspace;

import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.psi.ROSPktTypeBase;

/**
 * a class enabling references in ROS messages.
 */
public class ROSPackageReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(ROSPktTypeBase.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element,
                                                                 @NotNull ProcessingContext context) {
                        ROSPackageReference ref = searchForPkgRef(element);
                        if(ref == null) {
                            return PsiReference.EMPTY_ARRAY;
                        }
                        return new PsiReference[]{ref};
                    }

                    @Contract("null -> null")
                    private ROSPackageReference searchForPkgRef(PsiElement element) {
                        if(element instanceof ROSPktTypeBase && element.getText().contains("/")) {
                            int location = element.getText().indexOf('/');
                            TextRange range = new TextRange(0, location);
                            return new ROSPackageReference((ROSPktTypeBase) element, range);
                        }
                        return null;
                    }
                });
    }
}
