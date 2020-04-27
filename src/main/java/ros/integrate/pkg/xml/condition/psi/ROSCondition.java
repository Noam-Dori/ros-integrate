package ros.integrate.pkg.xml.condition.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.condition.ROSConditionFileType;
import ros.integrate.pkg.xml.condition.lang.ROSConditionLanguage;

public class ROSCondition extends PsiFileBase {
    public ROSCondition(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, ROSConditionLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return ROSConditionFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "ROS Condition";
    }
}
