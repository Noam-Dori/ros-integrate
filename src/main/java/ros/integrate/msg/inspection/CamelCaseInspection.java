package ros.integrate.msg.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.msg.annotate.ROSMsgTypeAnnotator;
import ros.integrate.msg.intention.RenameTypeQuickFix;
import ros.integrate.msg.psi.ROSMsgFile;
import ros.integrate.msg.psi.ROSMsgField;

import java.util.List;

public class CamelCaseInspection extends AbstractROSMsgInspection {

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

    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull final InspectionManager manager, final boolean isOnTheFly) {
        if (!(file instanceof ROSMsgFile)) return null;
        final List<ROSMsgField> fields = ((ROSMsgFile)file).getFields();
        final List<ProblemDescriptor> descriptors = new SmartList<>();
        for (ROSMsgField field : fields) {
            if(isSuppressedFor(field)) {continue;}
            ProgressManager.checkCanceled();
            PsiElement custom = field.getType().custom();
            if (custom != null && ROSMsgTypeAnnotator.getIllegalTypeMessage(custom.getText(),false) == null) {
                String message = getUnorthodoxTypeMessage(custom.getText(),false);
                if (message != null) {
                    ProblemDescriptor descriptor = manager.createProblemDescriptor(custom, custom, message,
                            ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly,
                            new RenameTypeQuickFix(camelCase(custom.getText())));
                    descriptors.add(descriptor);
                }
            }
        }
        return descriptors.toArray(new ProblemDescriptor[0]);
    }

    @Contract("null -> null")
    private static String camelCase(String text) { //TODO: add grammar processing to improve fix
        if(text == null) { return null;}
        if(Character.isLowerCase(text.charAt(0))) {
            text = Character.toUpperCase(text.charAt(0)) + (text.length() > 1 ? text.substring(1) : "");
        }
        for(int i = 0; i < text.length() - 1; i++) {
            if(text.charAt(i) == '_') {
                String newText = text.substring(0,i - 1) + Character.toUpperCase(text.charAt(i + 1));
                if (i < text.length() - 2) {text = newText + text.substring(i + 2);}
                else {text = newText;}
            }
            if(Character.isDigit(text.charAt(i)) && Character.isLowerCase(text.charAt(i + 1))) {
                String newText = text.substring(0,i) + Character.toUpperCase(text.charAt(i + 1));
                if (i < text.length() - 2) {text = newText + text.substring(i + 2);}
                else {text = newText;}
            }
        }
        return text;
    }
}
