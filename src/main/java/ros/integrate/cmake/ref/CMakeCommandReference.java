package ros.integrate.cmake.ref;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.cmake.CMakeClasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CMakeCommandReference extends PsiReferenceBase<PsiElement> {
    public CMakeCommandReference(PsiElement cmdElement) {
        super(cmdElement, cmdElement.getFirstChild().getTextRangeInParent());
    }

    @Override
    public @Nullable PsiElement resolve() {
        String cmdName = myElement.getFirstChild().getText();
        return findCommands(myElement.getContainingFile(), "function", "macro")
                .stream().map(cmd -> deepSearch(cmd, CMakeClasses.getUnquotedArgClass(), 2).get(0))
                .filter(arg -> arg.getText().equals(cmdName))
                .findFirst().orElse(null);
    }

    @NotNull
    private static List<PsiElement> findCommands(@NotNull PsiElement cmakeFileOrBlock, String... cmdNames) {
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

    @NotNull
    private static <T extends PsiElement> List<T> deepSearch(PsiElement element, Class<? extends T> targetClazz, int maxLevel) {
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
