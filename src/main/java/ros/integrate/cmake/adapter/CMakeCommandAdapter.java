package ros.integrate.cmake.adapter;

import com.intellij.psi.PsiElement;
import ros.integrate.cmake.psi.CMakeCommand;

import java.util.ArrayList;
import java.util.List;

public class CMakeCommandAdapter {
    private final PsiElement raw;

    public CMakeCommandAdapter(PsiElement rawCommand) {
        this.raw = rawCommand;
    }

    public List<CMakeArgumentAdapter> getArguments() {
        if (raw instanceof CMakeCommand) {
            return ((CMakeCommand) raw).getArguments().stream().map(CMakeArgumentAdapter::new).toList();
        } else {
            return new ArrayList<>(); // TODO adapt to CLion. properly.
        }
    }

    public boolean isCustomCommand() {
        return true; // TODO
    }
}
