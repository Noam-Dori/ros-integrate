package ros.integrate.pkt.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileEditor.ex.IdeDocumentHistory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Contract;
import ros.integrate.pkt.dialogue.NewROSMsgDialogue;
import ros.integrate.pkt.file.ROSMsgFileType;
import ros.integrate.pkt.psi.ROSPktElementFactory;
import ros.integrate.pkt.psi.ROSMsgFile;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.psi.ROSPktFile;
import ros.integrate.workspace.ROSPackageManager;
import ros.integrate.workspace.psi.ROSPackage;

/**
 * a fix used to add new ROS messages when needed.
 */
public class AddROSMsgQuickFix extends BaseIntentionAction {

    public AddROSMsgQuickFix(PsiElement fieldType) {
        type = fieldType;
        origPkgName = ((ROSPktFile)type.getContainingFile().getOriginalFile()).getPackage().getName();
    }

    private PsiElement type;
    private String origPkgName;

    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS Message/Service errors";
    }

    @NotNull
    @Override
    public String getText() {
        return "Create new ROS Message in project";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file)
            throws IncorrectOperationException {
        PsiDocumentManager.getInstance(project).commitAllDocuments();

        Pair<String,String> fullMsgName = getFullName();
        ROSPackageManager manager = ServiceManager.getService(project,ROSPackageManager.class);
        ROSPackage pkg = manager.findPackage(fullMsgName.first);

        NewROSMsgDialogue dialogue = new NewROSMsgDialogue(project, pkg == null || !pkg.isEditable() || pkg.getMsgRoot() == null ?
                file.getContainingDirectory() : pkg.getMsgRoot(),fullMsgName.second);
        dialogue.show();
        if(dialogue.getExitCode() != DialogWrapper.OK_EXIT_CODE) {
            return;
        }
        ApplicationManager.getApplication().runWriteAction(() -> {

            ROSMsgFile newMsg = (ROSMsgFile) ROSPktElementFactory.createFile(dialogue.getFileName(), dialogue.getDirectory(), ROSMsgFileType.INSTANCE).getOriginalFile();
            newMsg.setPackage(pkg == null ? ROSPackage.ORPHAN : pkg);

            IdeDocumentHistory.getInstance(project).includeCurrentPlaceAsChangePlace();

            if (!type.getText().equals(newMsg.getQualifiedName())) {
                type.replace(ROSPktElementFactory.createType(project,
                        newMsg.getPackage().getName().equals(origPkgName) ? newMsg.getName() : newMsg.getQualifiedName()));
            }

            OpenFileDescriptor descriptor = new OpenFileDescriptor(newMsg.getProject(), newMsg.getVirtualFile());
            FileEditorManager.getInstance(newMsg.getProject()).openTextEditor(descriptor, true);
        });
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    @NotNull
    @Contract(" -> new")
    private Pair<String,String> getFullName() {
        String msg = type.getText(), pkg;
        if(msg.contains("/")) {
            pkg = msg.replaceAll("/.*","");
            msg = msg.replaceAll(".*/","");
        } else {
            pkg = origPkgName;
        }
        return new Pair<>(pkg,msg);
    }
}
