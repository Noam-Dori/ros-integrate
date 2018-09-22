package ros.integrate.msg;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ros.integrate.msg.psi.ROSMsgFile;
import ros.integrate.msg.psi.ROSMsgLabel;
import ros.integrate.msg.psi.ROSMsgType;

public class ROSMsgRefactoringSupportProvider extends RefactoringSupportProvider {
    @Override
    public boolean isMemberInplaceRenameAvailable(@NotNull PsiElement element, PsiElement context) {
        return element instanceof ROSMsgLabel ||
                element instanceof ROSMsgType ||
                element instanceof ROSMsgFile;
    }
}
