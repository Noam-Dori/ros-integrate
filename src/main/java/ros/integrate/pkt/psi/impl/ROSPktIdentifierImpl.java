package ros.integrate.pkt.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.psi.ROSPktFile;
import ros.integrate.pkt.psi.ROSPktIdentifier;
import ros.integrate.pkt.psi.ROSPktSection;

/**
 * the base implementation of an identifiable named element in packet files.
 * @author Noam Dori
 */
public abstract class ROSPktIdentifierImpl extends ASTWrapperPsiElement implements ROSPktIdentifier {

    /**
     * constructs a new PSI element of this type
     * @param node a view into the token tree
     */
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

    @Override
    public ROSPktSection getContainingSection() {
        return (ROSPktSection) getParent().getParent();
    }
}
