// This is a generated file. Not intended for manual editing.
package ros.integrate.cmake.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;

public interface CMakeCommand extends CMakeOperation, PsiNameIdentifierOwner {

  @NotNull
  CMakeArgumentList getArgumentList();

  @NotNull
  CMakeCommandName getCommandName();

  @NotNull
  List<CMakeArgument> getArguments();

  @NotNull
  String getName();

  PsiElement setName(@NotNull String newName);

  @NotNull
  CMakeCommandName getNameIdentifier();

  @NotNull
  PsiReference getReference();

}
