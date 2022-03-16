// This is a generated file. Not intended for manual editing.
package ros.integrate.pkt.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ros.integrate.pkt.psi.ROSPktTypes.*;
import ros.integrate.pkt.psi.*;

public class ROSPktFieldImpl extends ROSPktFieldBaseImpl implements ROSPktField {

  public ROSPktFieldImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ROSPktVisitor visitor) {
    visitor.visitField(this);
  }

  @Override
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

  @Override
  @NotNull
  public ROSPktType getTypeBase() {
    return ROSPktPsiImplUtil.getTypeBase(this);
  }

  @Override
  public boolean isComplete() {
    return ROSPktPsiImplUtil.isComplete(this);
  }

}
