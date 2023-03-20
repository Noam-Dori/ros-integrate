package ros.integrate.cmake.adapter;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ros.integrate.cmake.CMakeUtil;

import java.util.List;

public class CMakeFileAdapter {
    private final PsiFile raw;

    public CMakeFileAdapter(@NotNull PsiFile rawCMakeFile) {
        this.raw = rawCMakeFile;
    }

    public List<CMakeCommandAdapter> findCommandCalls(String... commandNames) {
        return CMakeUtil.findCommands(raw, commandNames).stream().map(CMakeCommandAdapter::new).toList();
    }
}
