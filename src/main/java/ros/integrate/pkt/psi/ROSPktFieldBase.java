package ros.integrate.pkt.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ROSPktFieldBase extends PsiElement {
    @Nullable
    ROSPktConst getConst();

    @Nullable
    ROSPktLabel getLabel();

    @NotNull
    ROSPktTypeBase getTypeBase();

    boolean isLegalConstant();

    /**
     * whether or not the field is complete, or is some sort of fragment.
     * @return true is it is considered a valid type, false otherwise.
     */
    boolean isComplete();
}
