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
 * <p>checks if a message, service, or action type is not in the standard naming convention, and marks the type accordingly.</p>
 * <p>These are the naming conventions for a message, service, or action type name:</p>
 * <ol>
 *     <li>The message name is in PascalCase, meaning the words are glued together
 *         and every word starts with a capital letter.</li>
 *     <li>The only characters allowed are the alphabet (a-z,A-Z) and the digits (0-9)</li>
 * </ol>
 * <p>this inspection offers one fix:</p>
 * <ol>
 *     <li>Rename the type via rename refactor</li>
 * </ol>
 * @author Noam Dori
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
            return "Field type is not written in PascalCase";
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
                                field.getProject()).getSelectedEditor(), "type"));
                descriptors.add(descriptor);
            }
        }
    }
}
