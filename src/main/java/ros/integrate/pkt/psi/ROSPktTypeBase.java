package ros.integrate.pkt.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ROSPktTypeBase extends ROSPktIdentifier {
    @NotNull
    PsiElement raw();

    @Nullable
    PsiElement custom();

    int size();

    @SuppressWarnings("UnusedReturnValue")
    PsiElement removeArray();

    @NotNull
    PsiElement set(String rawType, int size);

    @NotNull
    PsiElement set(String rawType);

    String getName();

    @Nullable
    PsiElement getNameIdentifier();

    @NotNull
    PsiReference getReference();

    @NotNull
    PsiReference[] getReferences();

    /**
     * whether or not the type is complete, or is some sort of fragment.
     * @return true is it is considered a valid type, false otherwise.
     */
    boolean isComplete();
}
