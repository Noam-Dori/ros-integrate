// This is a generated file. Not intended for manual editing.
package ros.integrate.pkt.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class ROSPktVisitor extends PsiElementVisitor {

  public void visitComment(@NotNull ROSPktComment o) {
    visitPsiElement(o);
  }

  public void visitConst(@NotNull ROSPktConst o) {
    visitPsiElement(o);
  }

  public void visitField(@NotNull ROSPktField o) {
    visitFieldBase(o);
  }

  public void visitFieldFrag(@NotNull ROSPktFieldFrag o) {
    visitFieldBase(o);
  }

  public void visitLabel(@NotNull ROSPktLabel o) {
    visitIdentifier(o);
  }

  public void visitSeparator(@NotNull ROSPktSeparator o) {
    visitPsiElement(o);
  }

  public void visitType(@NotNull ROSPktType o) {
    visitTypeBase(o);
  }

  public void visitTypeFrag(@NotNull ROSPktTypeFrag o) {
    visitTypeBase(o);
  }

  public void visitFieldBase(@NotNull ROSPktFieldBase o) {
    visitPsiElement(o);
  }

  public void visitIdentifier(@NotNull ROSPktIdentifier o) {
    visitPsiElement(o);
  }

  public void visitTypeBase(@NotNull ROSPktTypeBase o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
