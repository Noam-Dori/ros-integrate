// This is a generated file. Not intended for manual editing.
package ros.integrate.cmake.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface CMakeArgumentList extends PsiElement {

  @NotNull
  List<CMakeBracketArgument> getBracketArgumentList();

  @NotNull
  List<CMakeBracketComment> getBracketCommentList();

  @NotNull
  List<CMakeQuotedArgument> getQuotedArgumentList();

  @NotNull
  List<CMakeUnquotedArgument> getUnquotedArgumentList();

}
