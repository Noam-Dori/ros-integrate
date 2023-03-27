package ros.integrate.cmake;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.refactoring.rename.inplace.MemberInplaceRenameHandler;
import com.intellij.refactoring.rename.inplace.MemberInplaceRenamer;
import org.jetbrains.annotations.NotNull;

public class CMakeArgumentInplaceRenameHandler extends MemberInplaceRenameHandler {
    @Override
    protected @NotNull MemberInplaceRenamer createMemberRenamer(@NotNull PsiElement element, @NotNull PsiNameIdentifierOwner elementToRename, @NotNull Editor editor) {
        if (CMakeClasses.getUnquotedArgClass().isInstance(element)) {
            return new MemberInplaceRenamer(elementToRename, element, editor);
        }
        return super.createMemberRenamer(element, elementToRename, editor);
    }
}
