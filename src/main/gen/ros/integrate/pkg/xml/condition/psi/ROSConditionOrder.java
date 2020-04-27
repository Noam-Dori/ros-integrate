// This is a generated file. Not intended for manual editing.
package ros.integrate.pkg.xml.condition.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ROSConditionOrder extends PsiElement {

  @NotNull
  List<ROSConditionItem> getItemList();

  @NotNull
  List<ROSConditionLogic> getLogicList();

  @NotNull
  List<ROSConditionOrder> getOrderList();

}
