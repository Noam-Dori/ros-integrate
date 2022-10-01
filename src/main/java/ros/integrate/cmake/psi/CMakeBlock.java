package ros.integrate.cmake.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface CMakeBlock extends CMakeOperation {

    default List<CMakeOperation> getCommandList() {
        List<CMakeOperation> allCommands = PsiTreeUtil.getChildrenOfTypeAsList(this, CMakeOperation.class);
        allCommands.remove(0);
        allCommands.remove(allCommands.size() - 1);
        return allCommands;
    }

    default CMakeCommand getStartCommand() {
        return PsiTreeUtil.getChildOfAnyType(this, CMakeCommand.class);
    }

    default CMakeCommand getEndCommand() {
        return getLastChildOfAnyType(this, CMakeCommand.class);
    }

    // getBlockArgs
    // getBlockType


    /**
     * Returns a direct child of the specified element having any of the specified classes.
     *
     * @param element the element to get the child for.
     * @param classes the array of classes.
     * @return the element, or {@code null} if none was found.
     */
    @SafeVarargs
    @Contract("null, _ -> null")
    static @Nullable
    <T extends PsiElement> T getLastChildOfAnyType(@Nullable PsiElement element, Class<? extends T> ... classes) {
        if (element == null) return null;
        for (PsiElement child = element.getLastChild(); child != null; child = child.getPrevSibling()) {
            for (Class<? extends T> aClass : classes) {
                if (aClass.isInstance(child)) {
                    return aClass.cast(child);
                }
            }
        }
        return null;
    }
}
