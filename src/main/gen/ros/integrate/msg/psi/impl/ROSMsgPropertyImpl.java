// This is a generated file. Not intended for manual editing.
package ros.integrate.msg.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ros.integrate.msg.psi.ROSMsgTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import ros.integrate.msg.psi.*;
import com.intellij.navigation.ItemPresentation;

public class ROSMsgPropertyImpl extends ASTWrapperPsiElement implements ROSMsgProperty {

  public ROSMsgPropertyImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ROSMsgVisitor visitor) {
    visitor.visitProperty(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ROSMsgVisitor) accept((ROSMsgVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ROSMsgConst getConst() {
    return findChildByClass(ROSMsgConst.class);
  }

  @Nullable
  public String getGeneralType() {
    return ROSMsgPsiImplUtil.getGeneralType(this);
  }

  @Nullable
  public String getType() {
    return ROSMsgPsiImplUtil.getType(this);
  }

  public PsiElement setType(String newName) {
    return ROSMsgPsiImplUtil.setType(this, newName);
  }

  @Nullable
  public String getName() {
    return ROSMsgPsiImplUtil.getName(this);
  }

  public int getArraySize() {
    return ROSMsgPsiImplUtil.getArraySize(this);
  }

  public PsiElement removeArray() {
    return ROSMsgPsiImplUtil.removeArray(this);
  }

  @Nullable
  public String getCConst() {
    return ROSMsgPsiImplUtil.getCConst(this);
  }

  @NotNull
  public ItemPresentation getPresentation() {
    return ROSMsgPsiImplUtil.getPresentation(this);
  }

  public boolean canHandle(@NotNull ROSMsgConst msgConst) {
    return ROSMsgPsiImplUtil.canHandle(this, msgConst);
  }

}
