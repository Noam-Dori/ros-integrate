package ros.integrate.cmake.adapter;

import com.intellij.psi.PsiElement;

public class CMakeArgumentAdapter {
    private final PsiElement raw;

    public CMakeArgumentAdapter(PsiElement rawArgument) {
        this.raw = rawArgument;
    }

    public String getText() {
        return raw.getText();
    }
}
