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

  @Nullable
  String getName();

  int getArraySize();

  PsiElement removeArray();

  @Nullable
  String getCConst();

  @NotNull
  ItemPresentation getPresentation();

  boolean canHandle(@NotNull ROSMsgConst msgConst);

}
