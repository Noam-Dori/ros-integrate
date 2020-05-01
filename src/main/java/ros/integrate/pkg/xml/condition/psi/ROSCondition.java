package ros.integrate.pkg.xml.condition.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.xml.condition.ROSConditionFileType;
import ros.integrate.pkg.xml.condition.lang.ROSConditionLanguage;

import java.util.List;

public class ROSCondition extends PsiFileBase implements ROSConditionExpr {
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
        return getText();
    }

    @NotNull
    public List<ROSConditionToken> getTokens() {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, ROSConditionToken.class);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        return other instanceof ROSCondition && getText().equals(((ROSCondition) other).getText());
    }
}
