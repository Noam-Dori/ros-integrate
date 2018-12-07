package ros.integrate.pkt.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.psi.ROSPktFile;
import ros.integrate.pkt.psi.ROSPktIdentifier;

public abstract class ROSPktIdentifierImpl extends ASTWrapperPsiElement implements ROSPktIdentifier {
    ROSPktIdentifierImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public ROSPktFile getContainingFile() {
        return (ROSPktFile) super.getContainingFile();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return this;
    }

    @Override
    public PsiElement setName(@NotNull String name) {
        return set(name);
    }
}
