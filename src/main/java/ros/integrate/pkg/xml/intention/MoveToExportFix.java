package ros.integrate.pkg.xml.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ExportTag;
import ros.integrate.pkg.xml.ROSPackageXml;

public class MoveToExportFix extends BaseIntentionAction {
    @NotNull
    private final ROSPackageXml pkgXml;
    @NotNull
    private final XmlTag origTag;

    @Contract(pure = true)
    public MoveToExportFix(@NotNull XmlTag lvl1Tag, @NotNull ROSPackageXml pkgXml) {
        this.pkgXml = pkgXml;
        origTag = lvl1Tag;
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS XML";
    }

    @NotNull
    @Override
    public String getText() {
        return "Move to export tag";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull Editor editor, PsiFile file) throws IncorrectOperationException {
        ExportTag export = pkgXml.getExport();
        if (export == null) {
            pkgXml.setExport(origTag.createChildTag("export", null, origTag.getText(), false));
        } else {
            export.getRawTag().addSubTag(origTag.createChildTag(origTag.getName(), null,
                    origTag.getValue().getText(), false), false);
        }
        origTag.delete();
    }
}
