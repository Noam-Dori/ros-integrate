package ros.integrate.pkt.psi.impl;

import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.psi.ROSPktFieldBase;
import ros.integrate.pkt.psi.ROSPktSection;

import java.util.List;
import java.util.stream.Collectors;

/**
 * a utility class holding {@link ROSPktSection} implementations
 * @author Noam Dori
 */
class ROSPktSectionUtil {
    /**
     * gets all available (and valid) fields in this section.
     *
     * @param section          the section to search for fields
     * @param queryClass       the class of which to search. If limited to complete fields, use
     *                         {@link ros.integrate.pkt.psi.ROSPktField}
     *                         if fragments need be searched use {@link ros.integrate.pkt.psi.ROSPktFieldFrag}.
     *                         if you want both, use {@link ros.integrate.pkt.psi.ROSPktFieldBase}
     * @param includeConstants whether or not constant fields should be included
     * @return a list of all available fields in this section in textual order.
     */
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
