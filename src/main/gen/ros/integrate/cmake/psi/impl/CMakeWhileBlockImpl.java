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

public class CMakeWhileBlockImpl extends ASTWrapperPsiElement implements CMakeWhileBlock {

  public CMakeWhileBlockImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CMakeVisitor visitor) {
    visitor.visitWhileBlock(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CMakeVisitor) accept((CMakeVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<CMakeBracketComment> getBracketCommentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CMakeBracketComment.class);
  }

  @Override
  @NotNull
  public List<CMakeForBlock> getForBlockList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CMakeForBlock.class);
  }

  @Override
  @NotNull
  public List<CMakeIfBlock> getIfBlockList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CMakeIfBlock.class);
  }

  @Override
  @NotNull
  public List<CMakeLineComment> getLineCommentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CMakeLineComment.class);
  }

  @Override
  @NotNull
  public List<CMakeWhileBlock> getWhileBlockList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CMakeWhileBlock.class);
  }

}
