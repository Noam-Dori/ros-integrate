package ros.integrate.pkt.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.psi.ROSPktTypeBase;

/**
 * the template used to implement the basic concept of a packet field type
 * @author Noam Dori
 */
public abstract class ROSPktTypeBaseImpl extends ROSPktIdentifierImpl implements ROSPktTypeBase {
    ROSPktTypeBaseImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    public PsiElement raw() {
        return ROSPktPsiImplUtil.raw(this);
    }

    @Nullable
    public PsiElement custom() {
        return ROSPktPsiImplUtil.custom(this);
    }

    public int size() {
        return ROSPktPsiImplUtil.size(this);
    }

    public PsiElement removeArray() {
        return ROSPktPsiImplUtil.removeArray(this);
    }

    @NotNull
    public PsiElement set(String rawType, int size) {
        return ROSPktPsiImplUtil.set(this, rawType, size);
    }

    @NotNull
    public PsiElement set(String rawType) {
        return ROSPktPsiImplUtil.set(this, rawType);
    }

    public String getName() {
        return ROSPktPsiImplUtil.getName(this);
    }

    @Nullable
    public PsiElement getNameIdentifier() {
        return ROSPktPsiImplUtil.getNameIdentifier(this);
    }

    @NotNull
    public PsiReference getReference() {
        return ROSPktPsiImplUtil.getReference(this);
    }

    @NotNull
    public PsiReference[] getReferences() {
        return ROSPktPsiImplUtil.getReferences(this);
    }

    @Override
    public boolean isComplete() {
        return false;
    }
}
