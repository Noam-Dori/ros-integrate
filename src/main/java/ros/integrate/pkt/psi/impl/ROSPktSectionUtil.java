package ros.integrate.pkt.psi.impl;

import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.psi.ROSPktFieldBase;
import ros.integrate.pkt.psi.ROSPktSection;

import java.util.List;

class ROSPktSectionUtil {
     @NotNull
     static <T extends ROSPktFieldBase> List<T> getFields(ROSPktSection section, Class<T> queryClass) {
         return PsiTreeUtil.getChildrenOfTypeAsList(section, queryClass);
     }
}
