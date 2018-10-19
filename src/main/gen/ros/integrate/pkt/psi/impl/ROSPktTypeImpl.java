// This is a generated file. Not intended for manual editing.
package ros.integrate.pkt.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import ros.integrate.pkt.psi.*;
import com.intellij.psi.PsiReference;

public class ROSPktTypeImpl extends ROSPktIdentifierImpl implements ROSPktType {

  public ROSPktTypeImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ROSPktVisitor visitor) {
    visitor.visitType(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ROSPktVisitor) accept((ROSPktVisitor)visitor);
    else super.accept(visitor);
  }

  @NotNull
  public PsiElement raw() {
    return ROSPktPsiImplUtil.raw(this);
  }

  @Nullable
  public PsiElement custom() {
    return ROSPktPsiImplUtil.custom(this);
  }

  public int size() {
    return ROSPktPsiImplUtil.size(this);
  }

  public PsiElement removeArray() {
    return ROSPktPsiImplUtil.removeArray(this);
  }

  @NotNull
  public PsiElement set(String rawType, int size) {
    return ROSPktPsiImplUtil.set(this, rawType, size);
  }

  @NotNull
  public PsiElement set(String rawType) {
    return ROSPktPsiImplUtil.set(this, rawType);
  }

  public String getName() {
    return ROSPktPsiImplUtil.getName(this);
  }

  @Nullable
  public PsiElement getNameIdentifier() {
    return ROSPktPsiImplUtil.getNameIdentifier(this);
  }

  @NotNull
  public PsiReference getReference() {
    return ROSPktPsiImplUtil.getReference(this);
  }

  @NotNull
  public PsiReference[] getReferences() {
    return ROSPktPsiImplUtil.getReferences(this);
  }

}
