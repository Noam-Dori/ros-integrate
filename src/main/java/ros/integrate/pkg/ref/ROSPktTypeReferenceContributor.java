package ros.integrate.pkg.ref;

import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.psi.ROSPktTypeBase;

/**
 * enables references from ROS messages to ROS packages.
 * @author Noam Dori
 */
public class ROSPktTypeReferenceContributor extends PsiReferenceContributor {
    @Nullable
    private ROSPackageReferenceBase<? extends PsiElement> getPackageReference(@NotNull PsiElement element) {
        if(element instanceof ROSPktTypeBase && element.getText().contains("/")) {
            int location = element.getText().indexOf('/');
            TextRange range = new TextRange(0, location);
            return new ROSPktToPackageReference((ROSPktTypeBase) element, range);
        }
        return null;
    }

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(ROSPktTypeBase.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element,
                                                                 @NotNull ProcessingContext context) {
                        ROSPackageReferenceBase<?> ref = getPackageReference(element);
                        if(ref == null) {
                            return PsiReference.EMPTY_ARRAY;
                        }
                        return new PsiReference[] {ref};
                    }
                });
    }
}
