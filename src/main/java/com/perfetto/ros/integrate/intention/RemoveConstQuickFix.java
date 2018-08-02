package com.perfetto.ros.integrate.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import com.perfetto.ros.integrate.psi.ROSMsgProperty;
import com.perfetto.ros.integrate.psi.ROSMsgTypes;
import org.jetbrains.annotations.NotNull;

public class RemoveConstQuickFix extends BaseIntentionAction {

    public RemoveConstQuickFix(ROSMsgProperty constToRemove) {
        rosMsg = constToRemove;
    }

    private ROSMsgProperty rosMsg;

    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS Message/Service errors";
    }

    @NotNull
    @Override
    public String getText() {
        return "Remove constant";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file)
            throws IncorrectOperationException {
        ApplicationManager.getApplication().invokeLater(() ->
                WriteCommandAction.writeCommandAction(project).run(() -> {
                    ASTNode start = rosMsg.getNode().findChildByType(ROSMsgTypes.CONST_ASSIGNER),
                            end = rosMsg.getNode().findChildByType(ROSMsgTypes.CONST);
                    if (start != null && end != null) {
                        rosMsg.deleteChildRange(start.getPsi(),end.getPsi());
                    }
                }));
    }
}
