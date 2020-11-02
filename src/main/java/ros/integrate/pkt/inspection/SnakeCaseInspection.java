package ros.integrate.pkt.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.fileEditor.FileEditorManager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.annotate.ROSPktLabelAnnotator;
import ros.integrate.pkt.intention.RenameTypeQuickFix;
import ros.integrate.pkt.psi.ROSPktFieldBase;
import ros.integrate.pkt.psi.ROSPktLabel;

import java.util.List;

/**
 * <p>checks if a message, service, or action label is not in the standard naming convention, and marks the field name accordingly.</p>
 * <p>These are the naming conventions for a message, service, or action label:</p>
 * <ol>
 *     <li>The message name is in snake_case, meaning the words are delimited by _</li>
 *     <li>The only characters allowed are the alphabet (a-z), the underscore (_) and the digits (0-9)</li>
 * </ol>
 * <p>this inspection offers one fix:</p>
 * <ol>
 *     <li>Rename the label via rename refactor</li>
 * </ol>
 * @author Noam Dori
 */
public class SnakeCaseInspection extends ROSPktInspectionBase {
    /**
     * fetches the message for the unorthodox label if available.
     * @param fieldLabel the name of the type to check.
     * @return null if the type is fine, otherwise the reason this type is named not according to ROS standards.
     */
    @Contract(pure = true)
    @Nullable
    public static String getUnorthodoxLabelMessage(@NotNull String fieldLabel) {
        String snakeCase = "[A-Za-z][0-9A-Za-z]*(_[0-9A-Za-z]+)*";
        if(!fieldLabel.matches(snakeCase)) {
            return "Field name is not written in snake_case";
        }
        return null;
    }

    @Override
    protected void checkField(@NotNull ROSPktFieldBase field, @NotNull InspectionManager manager, boolean isOnTheFly, @NotNull List<ProblemDescriptor> descriptors) {
        ROSPktLabel label = field.getLabel();
        if (label != null && ROSPktLabelAnnotator.getIllegalLabelMessage(label.getText()) == null) {
            String message = getUnorthodoxLabelMessage(label.getText());
            if (message != null) {
                ProblemDescriptor descriptor = manager.createProblemDescriptor(label, label, message,
                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly/*,
                        new RenameTypeQuickFix(FileEditorManager.getInstance(
                                field.getProject()).getSelectedEditor())*/);
                descriptors.add(descriptor);
            }
        }
    }
}
