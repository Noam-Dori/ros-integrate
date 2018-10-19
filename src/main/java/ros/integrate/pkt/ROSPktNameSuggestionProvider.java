package ros.integrate.pkt;

import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.SuggestedNameInfo;
import com.intellij.refactoring.rename.PreferrableNameSuggestionProvider;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.psi.ROSPktType;

import java.util.*;

/**
 * a class used to suggest names for things in ROS messages.
 */
public class ROSPktNameSuggestionProvider extends PreferrableNameSuggestionProvider {
    @Nullable
    @Override
    public SuggestedNameInfo getSuggestedNames(PsiElement fieldName, @Nullable PsiElement fieldType, Set<String> result) {
        Stack<String> parts = new Stack<>();
        String list = null;
        // for type "ClassType" suggest "type" and "class_type"
        // for type "Type[]" suggest "type_list" or "types"
        // for type "type[?]" suggest "type_array" or "types"
        // all of these are unless one exists already as such.
        if(fieldType instanceof ROSPktType) { // context should be the complete type
            String fullType = fieldType.getText().replaceFirst(".*/","");
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
        }

        return SuggestedNameInfo.NULL_INFO;
    }
}
