package ros.integrate.cmake.ref;

import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.PsiFileReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.cmake.adapter.CMakeArgumentAdapter;

import java.util.Optional;

/**
 * ROS1 is weird in the way it decided to split the full path into directory and file. This takes care of that issue.
 */
public class ROSCmakeToPktReference extends PsiReferenceBase<PsiElement> implements PsiFileReference {
    private final String dirHint;

    public ROSCmakeToPktReference(@NotNull CMakeArgumentAdapter element, @Nullable String dirHint) {
        super(element.raw());
        this.dirHint = dirHint == null ? "msg" : dirHint;
    }

    @Override
    public @Nullable PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        var path = myElement.getContainingFile().getVirtualFile().toNioPath().getParent()
                        .resolve(dirHint).resolve(myElement.getText());
        var vFile = VirtualFileManager.getInstance().findFileByNioPath(path);
        return Optional.ofNullable(vFile).map(PsiManager.getInstance(myElement.getProject())::findFile)
                .map(file -> new ResolveResult[]{new PsiElementResolveResult(file)})
                .orElse(ResolveResult.EMPTY_ARRAY);
    }
}
