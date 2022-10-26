package ros.integrate.cmake.ref;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.Nullable;
import ros.integrate.cmake.CMakeClasses;
import ros.integrate.cmake.CMakeUtil;

public class CMakeCommandReference extends PsiReferenceBase<PsiElement> {
    public CMakeCommandReference(PsiElement cmdElement) {
        super(cmdElement, cmdElement.getFirstChild().getTextRangeInParent());
    }

    @Override
    public @Nullable PsiElement resolve() {
        String cmdName = myElement.getFirstChild().getText();
        return CMakeUtil.findCommands(myElement.getContainingFile(), "function", "macro")
                .stream().map(cmd -> CMakeUtil.deepSearch(cmd, CMakeClasses.getArgClass(), 2).get(0))
                .filter(arg -> arg.getText().equals(cmdName))
                .findFirst().orElse(null);
    }


}
