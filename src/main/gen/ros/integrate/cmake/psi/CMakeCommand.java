// This is a generated file. Not intended for manual editing.
package ros.integrate.cmake.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface CMakeCommand extends CMakeOperation {

  @NotNull
  CMakeArgumentList getArgumentList();

  @NotNull
  CMakeCommandName getCommandName();

  @NotNull
  List<CMakeArgument> getArguments();

}
