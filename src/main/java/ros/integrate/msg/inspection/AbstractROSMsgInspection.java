package ros.integrate.msg.inspection;

import com.intellij.codeInsight.daemon.HighlightDisplayKey;
import com.intellij.codeInspection.*;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;
import ros.integrate.msg.psi.ROSMsgComment;
import ros.integrate.msg.psi.ROSMsgProperty;

import java.util.Arrays;
import java.util.Objects;

public abstract class AbstractROSMsgInspection extends LocalInspectionTool implements CustomSuppressableInspectionTool {
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

    private static SuppressIntentionAction[] createSuppressActions(HighlightDisplayKey key) {
        SuppressQuickFix[] batchSuppressActions = new SuppressQuickFix[] {new SuppressFieldByCommentFix(key)};
        return SuppressIntentionActionFromFix.convertBatchToSuppressIntentionActions(batchSuppressActions);
    }

    protected boolean isSuppressedFor(ROSMsgProperty property) {
        ROSMsgComment annotation = ROSMsgSuppressionUtil.findAnnotation(property);
        if(annotation == null) {return false;}
        return Arrays.asList(Objects.requireNonNull(annotation.getAnnotationIds()).split(",")).contains(getShortName());
    }
}
