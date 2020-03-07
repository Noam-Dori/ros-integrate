package ros.integrate.pkg.xml.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ROSPackageXml;

public class FixVersionQuickFix extends BaseIntentionAction {
    private final ROSPackageXml pkgXml;

    public FixVersionQuickFix(ROSPackageXml pkgXml, String prefix) {
        this.pkgXml = pkgXml;
        setText(prefix + " package version");
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS XML";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        String version = pkgXml.getVersion();
        return version == null || !version.matches("\\d+\\.\\d+\\.\\d+");
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        String current = pkgXml.getVersion();
        if (current == null) {
            pkgXml.setVersion("1.0.0");
            return;
        }
        String[] sections = current.split("\\.");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            builder.append(getNumber(sections, i));
        }
        pkgXml.setVersion(builder.toString());
    }

    @NotNull
    private String getNumber(@NotNull String[] sections, int idx) {
        String num;
        if (sections.length <= idx || !sections[idx].matches("\\d+")) {
            if (idx == 0) {
                num = "1";
            } else {
                num = "0";
            }
        } else {
            num = Integer.valueOf(sections[idx]).toString();
        }
        return num + (idx == 2 ? "" : ".");
    }
}
