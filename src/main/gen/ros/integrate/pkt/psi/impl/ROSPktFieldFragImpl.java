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

public class ROSPktFieldFragImpl extends ROSPktFieldBaseImpl implements ROSPktFieldFrag {

  public ROSPktFieldFragImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ROSPktVisitor visitor) {
    visitor.visitFieldFrag(this);
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
  @Nullable
  public ROSPktLabel getLabel() {
    return findChildByClass(ROSPktLabel.class);
  }

  @Override
  @Nullable
  public ROSPktType getType() {
    return findChildByClass(ROSPktType.class);
  }

  @Override
  @Nullable
  public ROSPktTypeFrag getTypeFrag() {
    return findChildByClass(ROSPktTypeFrag.class);
  }

  @Override
  @NotNull
  public ROSPktTypeBase getTypeBase() {
    return ROSPktPsiImplUtil.getTypeBase(this);
  }

}
