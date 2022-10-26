package ros.integrate.cmake;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CMakeUtil {

    @NotNull
    public static List<PsiElement> findCommands(@NotNull PsiElement cmakeFileOrBlock, String... cmdNames) {
        List<PsiElement> result = new ArrayList<>();
        List<String> nameQuery = Arrays.asList(cmdNames);
        for (PsiElement child : cmakeFileOrBlock.getChildren()) {
            if (CMakeClasses.getCommandClass().isInstance(child) &&
                    (nameQuery.isEmpty() || nameQuery.contains(child.getFirstChild().getText()))) {
                result.add(child);
            } else if (CMakeClasses.getBlockClass().isInstance(child)) {
                result.addAll(findCommands(child, cmdNames));
            }
        }
        return result;
    }

    @SuppressWarnings("unused")
    @NotNull
    public static <T extends PsiElement> List<T> deepSearch(PsiElement element, Class<? extends T> targetClazz) {
        return deepSearch(element, targetClazz, -1);
    }


    @NotNull
    public static <T extends PsiElement> List<T> deepSearch(PsiElement element, Class<? extends T> targetClazz, int maxLevel) {
        List<T> result = new ArrayList<>();
        if (maxLevel == 0) {
            return result;
        }
        for (PsiElement child : element.getChildren()) {
            if (targetClazz.isInstance(child)) {
                result.add(targetClazz.cast(child));
            } else {
                result.addAll(deepSearch(child, targetClazz, maxLevel - 1));
            }
        }
        return result;
    }
}
