// This is a generated file. Not intended for manual editing.
package ros.integrate.pkt.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import ros.integrate.pkt.psi.*;
import com.intellij.navigation.ItemPresentation;

public class ROSPktFieldImpl extends ASTWrapperPsiElement implements ROSPktField {

  public ROSPktFieldImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ROSPktVisitor visitor) {
    visitor.visitField(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ROSPktVisitor) accept((ROSPktVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ROSPktConst getConst() {
    return findChildByClass(ROSPktConst.class);
  }

  @Override
  @NotNull
  public ROSPktLabel getLabel() {
    return findNotNullChildByClass(ROSPktLabel.class);
  }

  @Override
  @NotNull
  public ROSPktType getType() {
    return findNotNullChildByClass(ROSPktType.class);
  }

  @NotNull
  public ItemPresentation getPresentation() {
    return ROSPktPsiImplUtil.getPresentation(this);
  }

  public boolean isLegalConstant() {
    return ROSPktPsiImplUtil.isLegalConstant(this);
  }

}
