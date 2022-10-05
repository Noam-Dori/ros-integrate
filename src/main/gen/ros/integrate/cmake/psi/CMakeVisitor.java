// This is a generated file. Not intended for manual editing.
package ros.integrate.cmake.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class CMakeVisitor extends PsiElementVisitor {

  public void visitArgumentList(@NotNull CMakeArgumentList o) {
    visitPsiElement(o);
  }

  public void visitBracketArgument(@NotNull CMakeBracketArgument o) {
    visitArgument(o);
  }

  public void visitBracketComment(@NotNull CMakeBracketComment o) {
    visitPsiElement(o);
  }

  public void visitCommand(@NotNull CMakeCommand o) {
    visitOperation(o);
  }

  public void visitCommandName(@NotNull CMakeCommandName o) {
    visitPsiElement(o);
  }

  public void visitForBlock(@NotNull CMakeForBlock o) {
    visitBlock(o);
  }

  public void visitFunction(@NotNull CMakeFunction o) {
    visitBlock(o);
  }

  public void visitIfBlock(@NotNull CMakeIfBlock o) {
    visitBlock(o);
  }

  public void visitJunk(@NotNull CMakeJunk o) {
    visitPsiElement(o);
  }

  public void visitLineComment(@NotNull CMakeLineComment o) {
    visitPsiElement(o);
  }

  public void visitMacro(@NotNull CMakeMacro o) {
    visitBlock(o);
  }

  public void visitQuotedArgument(@NotNull CMakeQuotedArgument o) {
    visitArgument(o);
  }

  public void visitUnquotedArgument(@NotNull CMakeUnquotedArgument o) {
    visitArgument(o);
  }

  public void visitWhileBlock(@NotNull CMakeWhileBlock o) {
    visitBlock(o);
  }

  public void visitArgument(@NotNull CMakeArgument o) {
    visitPsiElement(o);
  }

  public void visitBlock(@NotNull CMakeBlock o) {
    visitPsiElement(o);
  }

  public void visitOperation(@NotNull CMakeOperation o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
