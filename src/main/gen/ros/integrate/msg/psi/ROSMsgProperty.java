// This is a generated file. Not intended for manual editing.
package ros.integrate.msg.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.navigation.ItemPresentation;

public interface ROSMsgProperty extends PsiElement {

  @Nullable
  ROSMsgConst getConst();

  @Nullable
  String getGeneralType();

  @Nullable
  String getType();

  PsiElement setType(String newName);

  int getArraySize();

  PsiElement removeArray();

  @Nullable
  String getCConst();

  ItemPresentation getPresentation();

  boolean canHandle(@NotNull ROSMsgConst msgConst);

}
