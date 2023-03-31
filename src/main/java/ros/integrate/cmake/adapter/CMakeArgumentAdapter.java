package ros.integrate.cmake.adapter;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.cmake.CMakeClasses;
import ros.integrate.cmake.psi.impl.CMakeElementFactory;
import ros.integrate.pkg.psi.ROSPackage;

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

    public CMakeCommandAdapter getCommand() {
        PsiElement rawCommand = raw.getParent().getParent();
        return new CMakeCommandAdapter(rawCommand);
    }

    public CMakeFileAdapter getFile() {
        return new CMakeFileAdapter(raw.getContainingFile());
    }

    @Nullable
    public ROSPackage getPackage() {
        return getFile().getPackage();
    }

    public boolean equals(@NotNull CMakeArgumentAdapter other) {
        return PsiManager.getInstance(raw.getProject()).areElementsEquivalent(raw, other.raw);
    }

    public int getTextLength() {
        return raw.getTextLength();
    }

    public PsiElement raw() {
        return raw;
    }
}
