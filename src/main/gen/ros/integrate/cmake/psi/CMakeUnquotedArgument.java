// This is a generated file. Not intended for manual editing.
package ros.integrate.cmake.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.openapi.util.TextRange;

public interface CMakeUnquotedArgument extends CMakeArgument, PsiNamedElement {

  TextRange getArgTextRange();

  @NotNull
  String getArgText();

  String getName();

  PsiElement setName(@NotNull String newName);

}
