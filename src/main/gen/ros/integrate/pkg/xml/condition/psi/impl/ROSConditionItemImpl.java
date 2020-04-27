// This is a generated file. Not intended for manual editing.
package ros.integrate.pkg.xml.condition.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ros.integrate.pkg.xml.condition.psi.ROSConditionTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import ros.integrate.pkg.xml.condition.psi.*;

public class ROSConditionItemImpl extends ASTWrapperPsiElement implements ROSConditionItem {

  public ROSConditionItemImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ROSConditionVisitor visitor) {
    visitor.visitItem(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ROSConditionVisitor) accept((ROSConditionVisitor)visitor);
    else super.accept(visitor);
  }

}
