package ros.integrate.workspace.psi.impl;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.psi.impl.file.PsiDirectoryImpl;
import org.jetbrains.annotations.NotNull;
import ros.integrate.workspace.psi.ROSDirectoryService;
import ros.integrate.workspace.psi.ROSPackage;

public class ROSDirectoryImpl extends PsiDirectoryImpl {
    public ROSDirectoryImpl(PsiManagerImpl manager, @NotNull VirtualFile file) {
        super(manager, file);
    }

    @Override
    public ItemPresentation getPresentation() {
        final ROSPackage aPackage = ROSDirectoryService.getInstance().getPackage(this);
        return aPackage != null && !StringUtil.isEmpty(aPackage.getName()) ? aPackage.getPresentation() : super.getPresentation();
    }
}
