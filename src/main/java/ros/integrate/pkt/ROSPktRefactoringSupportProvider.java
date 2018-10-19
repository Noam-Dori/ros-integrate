package ros.integrate.pkt;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.psi.ROSMsgFile;
import ros.integrate.pkt.psi.ROSPktLabel;
import ros.integrate.pkt.psi.ROSPktType;

/**
 * a class enabling in-place refactoring support in ROS messages.
 */
public class ROSPktRefactoringSupportProvider extends RefactoringSupportProvider {
    @Override
    public boolean isMemberInplaceRenameAvailable(@NotNull PsiElement element, PsiElement context) {
        return element instanceof ROSPktLabel ||
                element instanceof ROSPktType ||
                element instanceof ROSMsgFile;
    }
}
