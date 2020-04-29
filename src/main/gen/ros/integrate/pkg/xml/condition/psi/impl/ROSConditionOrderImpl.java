// This is a generated file. Not intended for manual editing.
package ros.integrate.pkg.xml.condition.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ros.integrate.pkg.xml.condition.psi.ROSConditionTypes.*;
import ros.integrate.pkg.xml.condition.psi.*;

public class ROSConditionOrderImpl extends ROSConditionExprImpl implements ROSConditionOrder {

  public ROSConditionOrderImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ROSConditionVisitor visitor) {
    visitor.visitOrder(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ROSConditionVisitor) accept((ROSConditionVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ROSConditionItem> getItemList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ROSConditionItem.class);
  }

  @Override
  @NotNull
  public List<ROSConditionLogic> getLogicList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ROSConditionLogic.class);
  }

  @Override
  @NotNull
  public List<ROSConditionOrder> getOrderList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ROSConditionOrder.class);
  }

}
