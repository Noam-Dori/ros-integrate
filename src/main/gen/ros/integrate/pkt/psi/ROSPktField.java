// This is a generated file. Not intended for manual editing.
package ros.integrate.pkt.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.navigation.ItemPresentation;

public interface ROSPktField extends PsiElement {

  @Nullable
  ROSPktConst getConst();

  @NotNull
  ROSPktLabel getLabel();

  @NotNull
  ROSPktType getType();

  @NotNull
  ItemPresentation getPresentation();

  boolean isLegalConstant();

}
