package ros.integrate.cmake.adapter;

import com.intellij.psi.PsiElement;
import ros.integrate.cmake.CMakeClasses;
import ros.integrate.cmake.psi.impl.CMakeElementFactory;

public class CMakeArgumentAdapter {
    private final PsiElement raw;

    public CMakeArgumentAdapter(PsiElement rawArgument) {
        this.raw = rawArgument;
    }

    public String getText() {
        return raw.getText();
    }

    public void setText(String name) {
        if (!CMakeClasses.CLION) {
            raw.replace(CMakeElementFactory.createArgument(raw.getProject(), name));
        }
    }
}
