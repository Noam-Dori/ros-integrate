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
import com.intellij.psi.PsiReference;

public class ROSMsgTypeImpl extends ROSMsgIdentifierImpl implements ROSMsgType {

  public ROSMsgTypeImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ROSMsgVisitor visitor) {
    visitor.visitType(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ROSMsgVisitor) accept((ROSMsgVisitor)visitor);
    else super.accept(visitor);
  }

  @NotNull
  public PsiElement raw() {
    return ROSMsgPsiImplUtil.raw(this);
  }

  @Nullable
  public PsiElement custom() {
    return ROSMsgPsiImplUtil.custom(this);
  }

  public int size() {
    return ROSMsgPsiImplUtil.size(this);
  }

  public PsiElement removeArray() {
    return ROSMsgPsiImplUtil.removeArray(this);
  }

  @NotNull
  public PsiElement set(String rawType, int size) {
    return ROSMsgPsiImplUtil.set(this, rawType, size);
  }

  @NotNull
  public PsiElement set(String rawType) {
    return ROSMsgPsiImplUtil.set(this, rawType);
  }

  public String getName() {
    return ROSMsgPsiImplUtil.getName(this);
  }

  @Nullable
  public PsiElement getNameIdentifier() {
    return ROSMsgPsiImplUtil.getNameIdentifier(this);
  }

  @NotNull
  public PsiReference getReference() {
    return ROSMsgPsiImplUtil.getReference(this);
  }

  @NotNull
  public PsiReference[] getReferences() {
    return ROSMsgPsiImplUtil.getReferences(this);
  }

}
