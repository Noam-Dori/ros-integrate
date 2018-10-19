package ros.integrate.msg;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ros.integrate.msg.psi.ROSMsgFile;
import ros.integrate.msg.psi.ROSPktLabel;
import ros.integrate.msg.psi.ROSPktType;

/**
 * a class enabling in-place refactoring support in ROS messages.
 */
public class ROSMsgRefactoringSupportProvider extends RefactoringSupportProvider {
    @Override
    public boolean isMemberInplaceRenameAvailable(@NotNull PsiElement element, PsiElement context) {
        return element instanceof ROSPktLabel ||
                element instanceof ROSPktType ||
                element instanceof ROSMsgFile;
    }
}
