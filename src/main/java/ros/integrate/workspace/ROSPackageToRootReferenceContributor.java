package ros.integrate.workspace;

import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.psi.ROSPktTypeBase;
import ros.integrate.workspace.psi.ROSPackage;

import java.util.Arrays;

/**
 * a class enabling references in ROS messages.
 */
public class ROSPackageToRootReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(ROSPktTypeBase.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element,
                                                                 @NotNull ProcessingContext context) {
                        if(element instanceof ROSPackage) {
                            return Arrays.stream(((ROSPackage) element).getRoots())
                                    .map(root -> new ROSPackageToRootReference((ROSPackage)element,root))
                                    .toArray(PsiReference[]::new);
                        }
                        return PsiReference.EMPTY_ARRAY;
                    }
                });
    }
}
