package ros.integrate.pkt;

import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.SuggestedNameInfo;
import com.intellij.refactoring.rename.PreferrableNameSuggestionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.psi.ROSPktFieldBase;
import ros.integrate.pkt.psi.ROSPktFile;
import ros.integrate.pkt.psi.ROSPktLabel;
import ros.integrate.pkt.psi.ROSPktTypeBase;

import java.util.*;

/**
 * implements name suggestion for named elements in packet files (.msg, .srv, .action)
 * @author Noam Dori
 */
public class ROSPktNameSuggestionProvider extends PreferrableNameSuggestionProvider {
    @Nullable
    @Override
    public SuggestedNameInfo getSuggestedNames(@NotNull PsiElement elementToRename, @Nullable PsiElement psiContext, @NotNull Set<String> result) {
        if (psiContext instanceof ROSPktTypeBase) { // context should be the complete type
            typeBasedSuggestions(result, psiContext, elementToRename instanceof ROSPktFile);
        } else if (psiContext instanceof ROSPktFile) {
            result.remove(((ROSPktFile) psiContext).getName());
            result.add(((ROSPktFile) psiContext).getPacketName());
        } else if (psiContext instanceof ROSPktLabel) {
            Stack<String> parts = split(elementToRename.getText());
            assembleSnakeCase(parts, result, null);
            typeBasedSuggestions(result, ((ROSPktFieldBase)psiContext.getParent()).getTypeBase(), false);
        }

        return SuggestedNameInfo.NULL_INFO;
    }

    Stack<String> split(@NotNull String fullType) {
        Stack<String> parts = new Stack<>();
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
        return parts;
    }

    void assembleSnakeCase(@NotNull Stack<String> parts, Set<String> result, String list) {
        StringBuilder builder = new StringBuilder();
        while (!parts.isEmpty()) {
            builder.insert(0, parts.pop());
            if (list != null) {
                result.add(builder.toString().toLowerCase() + "_" + list);
                result.add(builder.toString().toLowerCase() + "s");
            } else {
                result.add(builder.toString().toLowerCase());
            }
            builder.insert(0, "_");
        }
    }

    void assemblePascalCase(@NotNull Stack<String> parts, Set<String> result, String list) {
        StringBuilder builder = new StringBuilder();
        while (!parts.isEmpty()) {
            builder.insert(0, capitalize(parts.pop()));
            if (list != null) {
                result.add(builder.toString() + capitalize(list));
                result.add(builder.toString() + "s");
            } else {
                result.add(builder.toString());
            }
        }
    }

    String capitalize(@NotNull String string) {
        return string.substring(0,1).toUpperCase() + string.substring(1).toLowerCase();
    }

    void typeBasedSuggestions(Set<String> result, @NotNull PsiElement typeBase, boolean pascalCase) {
        // for type "ClassType" suggest "type" and "class_type"
        // for type "Type[]" suggest "type_list" or "types"
        // for type "type[?]" suggest "type_array" or "types"
        // all of these are unless one exists already as such.
        String fullType = typeBase.getText().replaceFirst(".*/", "");
        String list = null;
        // extract array part
        if (fullType.matches(".*\\[]")) {
            list = "list";
        } else if (fullType.matches(".*\\[[0-9]+]")) {
            list = "array";
        }
        fullType = fullType.replaceAll("\\[[0-9]*]", "");
        Stack<String> parts = split(fullType);
        if (pascalCase) {
            assemblePascalCase(parts, result, list);
        } else {
            assembleSnakeCase(parts, result, list);
        }
    }
}
