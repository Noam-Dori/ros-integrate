// This is a generated file. Not intended for manual editing.
package ros.integrate.msg.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ros.integrate.msg.psi.ROSMsgTypes.*;
import ros.integrate.msg.psi.*;

public class ROSMsgLabelImpl extends ROSMsgIdentifierImpl implements ROSMsgLabel {

  public ROSMsgLabelImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ROSMsgVisitor visitor) {
    visitor.visitLabel(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ROSMsgVisitor) accept((ROSMsgVisitor)visitor);
    else super.accept(visitor);
  }

  public PsiElement set(String newName) {
    return ROSMsgPsiImplUtil.set(this, newName);
  }

}
