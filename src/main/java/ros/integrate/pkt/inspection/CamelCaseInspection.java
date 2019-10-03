package ros.integrate.pkt.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.annotate.ROSPktTypeAnnotator;
import ros.integrate.pkt.intention.RenameTypeQuickFix;
import ros.integrate.pkt.psi.ROSPktFieldBase;

import java.util.List;

/**
 * An inspection checking that all message types are written according to the ROS standards, in CamelCase form.
 */
public class CamelCaseInspection extends ROSPktInspectionBase {
    /**
     * fetches the message for the unorthodox type if available.
     * @param fieldType the name of the type to check.
     * @param inProject whether or not this type was defined in the project.
     * @return null if the type is fine, otherwise the reason this type is named not according to ROS standards.
     */
    @Contract(pure = true)
    @Nullable
    public static String getUnorthodoxTypeMessage(@NotNull String fieldType, boolean inProject) {
        String camelCase = "([A-Za-z]|([0-9]+([A-Z]|$)))*";
        String regex = inProject ? "[A-Z]" + camelCase
                : "([a-zA-Z][a-zA-Z0-9_]*/)?[A-Z]" + camelCase;
        if(!fieldType.matches(regex)) {
            return "Field type is not written in CamelCase";
        }
        return null;
    }

    @Override
    protected void checkField(@NotNull ROSPktFieldBase field, @NotNull InspectionManager manager, boolean isOnTheFly, @NotNull List<ProblemDescriptor> descriptors) {
        PsiElement custom = field.getTypeBase().custom();
        if (custom != null && ROSPktTypeAnnotator.getIllegalTypeMessage(custom.getText(),false) == null) {
            String message = getUnorthodoxTypeMessage(custom.getText(),false);
            if (message != null) {
                ProblemDescriptor descriptor = manager.createProblemDescriptor(custom, custom, message,
                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly,
                        new RenameTypeQuickFix(FileEditorManager.getInstance(
                                field.getProject()).getSelectedEditor()));
                descriptors.add(descriptor);
            }
        }
    }

    /* // if forced rename ever becomes available, here is some starting code to camelcase a string
    @Contract("null -> null")
    private static String camelCase(String text) { //notTODO: add grammar processing to improve fix
        if(text == null) { return null;}
        if(Character.isLowerCase(text.charAt(0))) {
            text = Character.toUpperCase(text.charAt(0)) + (text.length() > 1 ? text.substring(1) : "");
        }
        for(int i = 0; i < text.length() - 1; i++) {
            if(text.charAt(i) == '_') {
                String newText = text.substring(0,i) + Character.toUpperCase(text.charAt(i + 1));
                if (i < text.length() - 2) {text = newText + text.substring(i + 2);}
                else {text = newText;}
            }
            if(Character.isDigit(text.charAt(i)) && Character.isLowerCase(text.charAt(i + 1))) {
                String newText = text.substring(0,i + 1) + Character.toUpperCase(text.charAt(i + 1));
                if (i < text.length() - 2) {text = newText + text.substring(i + 2);}
                else {text = newText;}
            }
        }
        return text;
    }
    */
}
