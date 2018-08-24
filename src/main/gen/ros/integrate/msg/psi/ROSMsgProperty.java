// This is a generated file. Not intended for manual editing.
package ros.integrate.msg.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.navigation.ItemPresentation;

public interface ROSMsgProperty extends PsiElement {

  @Nullable
  ROSMsgConst getConst();

  @NotNull
  ROSMsgFieldName getFieldName();

  @NotNull
  ROSMsgType getType();

  @Nullable
  String getCustomType();

  @Nullable
  String getRawType();

  PsiElement setType(String newName);

  PsiElement setFieldName(String newName);

  int getArraySize();

  PsiElement removeArray();

  @NotNull
  ItemPresentation getPresentation();

  boolean canHandle(@NotNull ROSMsgConst msgConst);

}
