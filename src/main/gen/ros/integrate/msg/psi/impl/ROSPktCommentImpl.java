// This is a generated file. Not intended for manual editing.
package ros.integrate.msg.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import ros.integrate.msg.psi.*;

public class ROSPktCommentImpl extends ASTWrapperPsiElement implements ROSPktComment {

  public ROSPktCommentImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ROSPktVisitor visitor) {
    visitor.visitComment(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ROSPktVisitor) accept((ROSPktVisitor)visitor);
    else super.accept(visitor);
  }

  @Nullable
  public String getAnnotationIds() {
    return ROSPktPsiImplUtil.getAnnotationIds(this);
  }

}
