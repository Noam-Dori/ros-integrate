// This is a generated file. Not intended for manual editing.
package ros.integrate.msg.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ROSMsgType extends ROSMsgIdentifier {

  @NotNull
  PsiElement raw();

  @Nullable
  PsiElement custom();

  int size();

  PsiElement removeArray();

  PsiElement set(String rawType, int size);

  PsiElement set(String rawType);

  String getName();

}
