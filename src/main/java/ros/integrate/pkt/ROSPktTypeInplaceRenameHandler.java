package ros.integrate.pkt;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.refactoring.rename.inplace.MemberInplaceRenameHandler;
import com.intellij.refactoring.rename.inplace.MemberInplaceRenamer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.psi.ROSPktFile;

public class ROSPktTypeInplaceRenameHandler extends MemberInplaceRenameHandler {
    @NotNull
    @Override
    protected MemberInplaceRenamer createMemberRenamer(@NotNull PsiElement element, @NotNull PsiNameIdentifierOwner elementToRename, @NotNull Editor editor) {
        if(elementToRename instanceof ROSPktFile) {

            return new MemberInplaceRenamer(elementToRename, element, editor,
                    pkt(element).getPacketName(), pkt(element).getPacketName());
        } else {
            return super.createMemberRenamer(element, elementToRename, editor);
        }
    }

    @Contract(pure = true)
    private static ROSPktFile pkt(PsiElement element) {
        return (ROSPktFile) element;
    }
}
