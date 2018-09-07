// This is a generated file. Not intended for manual editing.
package ros.integrate.msg.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.navigation.ItemPresentation;

public interface ROSMsgField extends PsiElement {

  @Nullable
  ROSMsgConst getConst();

  @NotNull
  ROSMsgLabel getLabel();

  @NotNull
  ROSMsgType getType();

  @NotNull
  ItemPresentation getPresentation();

  boolean isLegalConstant();

}
