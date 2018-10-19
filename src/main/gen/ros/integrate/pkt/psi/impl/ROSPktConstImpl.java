// This is a generated file. Not intended for manual editing.
package ros.integrate.pkt.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import ros.integrate.pkt.psi.*;

public class ROSPktConstImpl extends ASTWrapperPsiElement implements ROSPktConst {

  public ROSPktConstImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ROSPktVisitor visitor) {
    visitor.visitConst(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ROSPktVisitor) accept((ROSPktVisitor)visitor);
    else super.accept(visitor);
  }

  @NotNull
  public ROSPktType getBestFit() {
    return ROSPktPsiImplUtil.getBestFit(this);
  }

}
