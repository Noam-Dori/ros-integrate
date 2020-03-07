package ros.integrate.pkt;

import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.SuggestedNameInfo;
import com.intellij.refactoring.rename.PreferrableNameSuggestionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.psi.ROSPktFile;
import ros.integrate.pkt.psi.ROSPktTypeBase;

import java.util.*;

/**
 * a class used to suggest names for things in ROS messages.
 */
public class ROSPktNameSuggestionProvider extends PreferrableNameSuggestionProvider {
    @Nullable
    @Override
    public SuggestedNameInfo getSuggestedNames(@NotNull PsiElement elementToRename, @Nullable PsiElement psiContext, @NotNull Set<String> result) {
        Stack<String> parts = new Stack<>();
        String list = null;
        // for type "ClassType" suggest "type" and "class_type"
        // for type "Type[]" suggest "type_list" or "types"
        // for type "type[?]" suggest "type_array" or "types"
        // all of these are unless one exists already as such.
        if(psiContext instanceof ROSPktTypeBase) { // context should be the complete type
            String fullType = psiContext.getText().replaceFirst(".*/","");
            // extract array part
            if(fullType.matches(".*\\[]")) {
                list = "list";
            } else if(fullType.matches(".*\\[[0-9]+]")) {
                list = "array";
            }
            fullType = fullType.replaceAll("\\[[0-9]*]","");

            //split based on newCap if CamelCase, otherwise by under_score.
            for (int i = 0; i < fullType.length(); i++) {
                if (fullType.charAt(i) == '_') {
                    parts.add(fullType.substring(0, i));
                    fullType = fullType.substring(i + 1);
                    i = 0;
                } else if (i < fullType.length() - 1 &&
                        Character.isLowerCase(fullType.charAt(i)) &&
                        Character.isUpperCase(fullType.charAt(i + 1))) {
                    parts.add(fullType.substring(0, i + 1));
                    fullType = fullType.substring(i + 1);
                    i = 0;
                }
            }
            parts.add(fullType);

            StringBuilder builder = new StringBuilder();
            while (!parts.isEmpty()) {
                builder.insert(0,parts.pop());
                if(list != null) {
                    result.add(builder.toString().toLowerCase() + "_" + list);
                    result.add(builder.toString().toLowerCase() + "s");
                } else {
                    result.add(builder.toString().toLowerCase());
                }
                builder.insert(0,"_");
            }
        } else if(psiContext instanceof ROSPktFile) {
            result.remove(((ROSPktFile)psiContext).getName());
            result.add(((ROSPktFile)psiContext).getPacketName());
        }

        return SuggestedNameInfo.NULL_INFO;
    }
}
