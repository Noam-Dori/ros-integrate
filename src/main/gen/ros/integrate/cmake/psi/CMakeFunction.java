// This is a generated file. Not intended for manual editing.
package ros.integrate.cmake.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface CMakeFunction extends PsiElement {

  @NotNull
  List<CMakeBracketComment> getBracketCommentList();

  @NotNull
  List<CMakeForBlock> getForBlockList();

  @NotNull
  List<CMakeIfBlock> getIfBlockList();

  @NotNull
  List<CMakeLineComment> getLineCommentList();

  @NotNull
  List<CMakeWhileBlock> getWhileBlockList();

}
