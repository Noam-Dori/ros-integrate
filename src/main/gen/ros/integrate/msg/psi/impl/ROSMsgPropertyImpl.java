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

  @Override
  @NotNull
  public ROSMsgFieldName getFieldName() {
    return findNotNullChildByClass(ROSMsgFieldName.class);
  }

  @Override
  @NotNull
  public ROSMsgType getType() {
    return findNotNullChildByClass(ROSMsgType.class);
  }

  @Nullable
  public String getCustomType() {
    return ROSMsgPsiImplUtil.getCustomType(this);
  }

  @Nullable
  public String getRawType() {
    return ROSMsgPsiImplUtil.getRawType(this);
  }

  public PsiElement setType(String newName) {
    return ROSMsgPsiImplUtil.setType(this, newName);
  }

  public PsiElement setFieldName(String newName) {
    return ROSMsgPsiImplUtil.setFieldName(this, newName);
  }

  public int getArraySize() {
    return ROSMsgPsiImplUtil.getArraySize(this);
  }

  public PsiElement removeArray() {
    return ROSMsgPsiImplUtil.removeArray(this);
  }

  @NotNull
  public ItemPresentation getPresentation() {
    return ROSMsgPsiImplUtil.getPresentation(this);
  }

  public boolean canHandle(@NotNull ROSMsgConst msgConst) {
    return ROSMsgPsiImplUtil.canHandle(this, msgConst);
  }

}
