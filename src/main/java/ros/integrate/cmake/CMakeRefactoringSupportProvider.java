package ros.integrate.cmake;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.cmake.adapter.CMakeCommandAdapter;

public class CMakeRefactoringSupportProvider extends RefactoringSupportProvider {
    @Override
    public boolean isMemberInplaceRenameAvailable(@NotNull PsiElement element, @Nullable PsiElement context) {
        if (CMakeClasses.getUnquotedArgClass().isInstance(element)) {
            return true;
        }
        if (CMakeClasses.getCommandClass().isInstance(element)) {
            return new CMakeCommandAdapter(element).isCustomCommand();
        }
        return false;
    }
}
