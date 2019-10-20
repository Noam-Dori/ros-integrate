package ros.integrate.pkt.inspection;

import com.intellij.codeInsight.daemon.HighlightDisplayKey;
import com.intellij.codeInspection.*;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.psi.ROSPktFieldBase;
import ros.integrate.pkt.psi.ROSPktFile;

import java.util.List;

/**
 * the generic inspection within ROS message and service files.
 */
abstract class ROSPktInspectionBase extends LocalInspectionTool implements CustomSuppressableInspectionTool {
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
     *
     * @param key I dunno, check out {@link ROSPktInspectionBase#getSuppressActions(PsiElement)} to understand.
     * @return an array of possible suppression actions for this inspection.
     */
    @NotNull
    private static SuppressIntentionAction[] createSuppressActions(@NotNull HighlightDisplayKey key) {
        SuppressQuickFix[] batchSuppressActions = new SuppressQuickFix[]{new SuppressFieldByCommentFix(key)};
        return SuppressIntentionActionFromFix.convertBatchToSuppressIntentionActions(batchSuppressActions);
    }

    /**
     * is this field's inspections being suppressed?
     *
     * @param field the field to check
     * @return true if this field has a suppression annotation for this inspection
     */
    @SuppressWarnings("WeakerAccess") // extra useful for inspections that directly override checkFile
    protected boolean isSuppressedFor(@NotNull ROSPktFieldBase field) {
        return ROSPktSuppressionUtil.findAnnotation(field, getShortName()) != null;
    }

    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        if (!(file instanceof ROSPktFile)) return null;
        final List<ROSPktFieldBase> fields = ((ROSPktFile) file).getFields(ROSPktFieldBase.class);
        final List<ProblemDescriptor> descriptors = new SmartList<>();
        for (ROSPktFieldBase field : fields) {
            if (isSuppressedFor(field)) {
                continue;
            }
            ProgressManager.checkCanceled();
            checkField(field, manager, isOnTheFly, descriptors);
        }
        return descriptors.toArray(new ProblemDescriptor[0]);
    }

    /**
     * Override this to report problems at field level.
     *
     * @param field       to check.
     * @param manager     InspectionManager to ask for ProblemDescriptor's from.
     * @param isOnTheFly  true if called during on the fly editor highlighting. Called from Inspect Code action otherwise.
     * @param descriptors list of descriptors to append raised problems found for the field.
     */
    void checkField(@NotNull ROSPktFieldBase field, @NotNull InspectionManager manager,
                    boolean isOnTheFly, @NotNull List<ProblemDescriptor> descriptors) {
    }
}
