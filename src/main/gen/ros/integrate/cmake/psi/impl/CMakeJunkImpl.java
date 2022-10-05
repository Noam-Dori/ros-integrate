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

public class CMakeJunkImpl extends ASTWrapperPsiElement implements CMakeJunk {

  public CMakeJunkImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CMakeVisitor visitor) {
    visitor.visitJunk(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CMakeVisitor) accept((CMakeVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<CMakeBracketArgument> getBracketArgumentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CMakeBracketArgument.class);
  }

  @Override
  @NotNull
  public List<CMakeBracketComment> getBracketCommentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CMakeBracketComment.class);
  }

  @Override
  @NotNull
  public List<CMakeQuotedArgument> getQuotedArgumentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CMakeQuotedArgument.class);
  }

  @Override
  @NotNull
  public List<CMakeUnquotedArgument> getUnquotedArgumentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CMakeUnquotedArgument.class);
  }

}
