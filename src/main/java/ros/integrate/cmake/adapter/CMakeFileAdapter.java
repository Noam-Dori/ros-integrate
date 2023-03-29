package ros.integrate.cmake.adapter;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ros.integrate.cmake.CMakeUtil;
import ros.integrate.pkg.ROSPackageManager;
import ros.integrate.pkg.psi.ROSPackage;

import java.util.List;

public class CMakeFileAdapter {
    @NotNull
    private final PsiFile raw;

    public CMakeFileAdapter(@NotNull PsiFile rawCMakeFile) {
        this.raw = rawCMakeFile;
    }

    public List<CMakeCommandAdapter> findCommandCalls(String... commandNames) {
        return CMakeUtil.findCommands(raw, commandNames).stream().map(CMakeCommandAdapter::new).toList();
    }

    public ROSPackage getPackage() {
        return raw.getProject().getService(ROSPackageManager.class).findPackage(raw.getContainingDirectory());
    }

    /**
     * Re-configures the file so that it fits the designated package
     * @param newPkg the new package to match
     * @apiNote this does not move the file, you will have to do that yourself.
     */
    public void setPackage(@NotNull ROSPackage newPkg) {
        setPackage(newPkg.getName());
        newPkg.setCMakeLists(this);
    }

    public void setPackage(@NotNull String newPkgName) {
        for (var command : findCommandCalls("project")) {
            command.getArguments().get(0).setText(newPkgName);
        }
    }

    @NotNull
    public PsiFile raw() {
        return raw;
    }
}
