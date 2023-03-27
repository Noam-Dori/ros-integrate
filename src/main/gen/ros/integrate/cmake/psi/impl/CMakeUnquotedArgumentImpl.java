// This is a generated file. Not intended for manual editing.
package ros.integrate.cmake.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ros.integrate.cmake.psi.CMakeTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import ros.integrate.cmake.psi.*;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiReference;

public class CMakeUnquotedArgumentImpl extends ASTWrapperPsiElement implements CMakeUnquotedArgument {

  public CMakeUnquotedArgumentImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CMakeVisitor visitor) {
    visitor.visitUnquotedArgument(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CMakeVisitor) accept((CMakeVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  public TextRange getArgTextRange() {
    return CMakePsiImplUtil.getArgTextRange(this);
  }

  @Override
  public @NotNull String getArgText() {
    return CMakePsiImplUtil.getArgText(this);
  }

  @Override
  public String getName() {
    return CMakePsiImplUtil.getName(this);
  }

  @Override
  public PsiElement setName(@NotNull String newName) {
    return CMakePsiImplUtil.setName(this, newName);
  }

  @Override
  public @NotNull PsiReference @NotNull [] getReferences() {
    return CMakePsiImplUtil.getReferences(this);
  }

}
