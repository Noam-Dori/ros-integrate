package ros.integrate.pkt.psi.impl;

import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.psi.ROSPktFieldBase;
import ros.integrate.pkt.psi.ROSPktSection;

import java.util.List;
import java.util.stream.Collectors;

class ROSPktSectionUtil {
     @NotNull
     static <T extends ROSPktFieldBase> List<T> getFields(ROSPktSection section, Class<T> queryClass,
                                                          boolean includeConstants) {
         List<T> ret = PsiTreeUtil.getChildrenOfTypeAsList(section, queryClass);
         if (includeConstants) {
             return ret;
         }
         return ret.stream().filter(field -> field.getConst() == null).collect(Collectors.toList());
     }
}
