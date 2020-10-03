package ros.integrate.pkt.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.psi.ROSPktConst;
import ros.integrate.pkt.psi.ROSPktFieldBase;
import ros.integrate.pkt.psi.ROSPktFile;
import ros.integrate.pkt.psi.ROSPktLabel;

/**
 * the template used to implement the basic concept of a packet field
 * @author Noam Dori
 */
public abstract class ROSPktFieldBaseImpl extends ASTWrapperPsiElement implements ROSPktFieldBase {

    /**
     * constructs a new PSI element of this type
     * @param node a view into the token tree
     */
    ROSPktFieldBaseImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    @Nullable
    public ROSPktConst getConst() {
        return findChildByClass(ROSPktConst.class);
    }

    @Nullable
    public ROSPktLabel getLabel() {
        return findChildByClass(ROSPktLabel.class);
    }

    public boolean isLegalConstant() {
        return ROSPktPsiImplUtil.isLegalConstant(this);
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public ROSPktFile getContainingFile() {
        return (ROSPktFile) super.getContainingFile();
    }
}
