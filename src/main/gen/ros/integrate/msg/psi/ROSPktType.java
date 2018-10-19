// This is a generated file. Not intended for manual editing.
package ros.integrate.msg.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;

public interface ROSPktType extends ROSPktIdentifier {

  @NotNull
  PsiElement raw();

  @Nullable
  PsiElement custom();

  int size();

  PsiElement removeArray();

  @NotNull
  PsiElement set(String rawType, int size);

  @NotNull
  PsiElement set(String rawType);

  String getName();

  @Nullable
  PsiElement getNameIdentifier();

  @NotNull
  PsiReference getReference();

  @NotNull
  PsiReference[] getReferences();

}
