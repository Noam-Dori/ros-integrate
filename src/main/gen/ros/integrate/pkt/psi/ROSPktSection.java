// This is a generated file. Not intended for manual editing.
package ros.integrate.pkt.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ROSPktSection extends PsiElement {

  @NotNull
  List<ROSPktComment> getCommentList();

  @NotNull
  List<ROSPktField> getFieldList();

  @NotNull
  List<ROSPktFieldFrag> getFieldFragList();

  @NotNull
  <T extends ROSPktFieldBase> List<T> getFields(Class<T> queryClass, boolean includeConstants);

}
