package ros.integrate.cmake.adapter;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;
import ros.integrate.cmake.psi.CMakeCommand;
import ros.integrate.pkg.psi.ROSPackage;

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

    public String getName() {
        if (raw instanceof CMakeCommand) {
            return ((CMakeCommand) raw).getName();
        }
        return raw.getFirstChild().getText(); // TODO CLion ?
    }

    public boolean isCustomCommand() {
        return true; // TODO
    }

    public CMakeFileAdapter getFile() {
        return new CMakeFileAdapter(raw.getContainingFile());
    }

    @Nullable
    public ROSPackage getPackage() {
        return getFile().getPackage();
    }
}
