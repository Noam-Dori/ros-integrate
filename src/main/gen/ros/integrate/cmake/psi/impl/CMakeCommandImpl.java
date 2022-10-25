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
import com.intellij.psi.PsiReference;

public class CMakeCommandImpl extends ASTWrapperPsiElement implements CMakeCommand {

  public CMakeCommandImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CMakeVisitor visitor) {
    visitor.visitCommand(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CMakeVisitor) accept((CMakeVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public CMakeArgumentList getArgumentList() {
    return findNotNullChildByClass(CMakeArgumentList.class);
  }

  @Override
  @NotNull
  public CMakeCommandName getCommandName() {
    return findNotNullChildByClass(CMakeCommandName.class);
  }

  @Override
  @NotNull
  public List<CMakeArgument> getArguments() {
    return CMakePsiImplUtil.getArguments(this);
  }

  @Override
  @NotNull
  public String getName() {
    return CMakePsiImplUtil.getName(this);
  }

  @Override
  public PsiElement setName(@NotNull String newName) {
    return CMakePsiImplUtil.setName(this, newName);
  }

  @Override
  @NotNull
  public CMakeCommandName getNameIdentifier() {
    return CMakePsiImplUtil.getNameIdentifier(this);
  }

  @Override
  @NotNull
  public PsiReference getReference() {
    return CMakePsiImplUtil.getReference(this);
  }

}
