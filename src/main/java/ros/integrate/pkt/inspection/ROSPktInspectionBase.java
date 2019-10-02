package ros.integrate.pkt.inspection;

import com.intellij.codeInsight.daemon.HighlightDisplayKey;
import com.intellij.codeInspection.*;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.psi.ROSPktFieldBase;

/**
 * the generic inspection within ROS message and service files.
 */
public abstract class ROSPktInspectionBase extends LocalInspectionTool implements CustomSuppressableInspectionTool {
    @Nullable
    @Override
    public SuppressIntentionAction[] getSuppressActions(@Nullable PsiElement element) {
        String shortName = getShortName();
        HighlightDisplayKey key = HighlightDisplayKey.find(shortName);
        if (key == null) {
            throw new AssertionError("HighlightDisplayKey.find(" + shortName + ") is null. Inspection: " + getClass());
        }
        return createSuppressActions(key);
    }

    /**
     * creates a suppression action for this inspection
     * @param key I dunno, check out {@link ROSPktInspectionBase#getSuppressActions(PsiElement)} to understand.
     * @return an array of possible suppression actions for this inspection.
     */
    @NotNull
    private static SuppressIntentionAction[] createSuppressActions(@NotNull HighlightDisplayKey key) {
        SuppressQuickFix[] batchSuppressActions = new SuppressQuickFix[] {new SuppressFieldByCommentFix(key)};
        return SuppressIntentionActionFromFix.convertBatchToSuppressIntentionActions(batchSuppressActions);
    }

    /**
     * is this field's inspections being suppressed?
     * @param field the field to check
     * @return true if this field has a suppression annotation for this inspection
     */
    boolean isSuppressedFor(@NotNull ROSPktFieldBase field) {
        return ROSPktSuppressionUtil.findAnnotation(field, getShortName()) != null;
    }
}
