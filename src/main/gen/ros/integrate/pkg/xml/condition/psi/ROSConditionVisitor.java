// This is a generated file. Not intended for manual editing.
package ros.integrate.pkg.xml.condition.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class ROSConditionVisitor extends PsiElementVisitor {

  public void visitItem(@NotNull ROSConditionItem o) {
    visitExpr(o);
  }

  public void visitLogic(@NotNull ROSConditionLogic o) {
    visitToken(o);
  }

  public void visitOrder(@NotNull ROSConditionOrder o) {
    visitExpr(o);
  }

  public void visitExpr(@NotNull ROSConditionExpr o) {
    visitPsiElement(o);
  }

  public void visitToken(@NotNull ROSConditionToken o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
