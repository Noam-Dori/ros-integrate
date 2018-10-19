// This is a generated file. Not intended for manual editing.
package ros.integrate.msg.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import ros.integrate.msg.psi.*;

public class ROSPktLabelImpl extends ROSPktIdentifierImpl implements ROSPktLabel {

  public ROSPktLabelImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ROSPktVisitor visitor) {
    visitor.visitLabel(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ROSPktVisitor) accept((ROSPktVisitor)visitor);
    else super.accept(visitor);
  }

  public PsiElement set(String newName) {
    return ROSPktPsiImplUtil.set(this, newName);
  }

  public String getName() {
    return ROSPktPsiImplUtil.getName(this);
  }

}
