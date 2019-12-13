package ros.integrate.pkg.xml.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ROSPackageXml;

public class FixURLQuickFix extends BaseIntentionAction {
    @NotNull
    private final ROSPackageXml pkgXml;
    private final int id;

    @Contract(pure = true)
    public FixURLQuickFix(@NotNull ROSPackageXml pkgXml, int id) {
        this.id = id;
        this.pkgXml = pkgXml;
    }

    @NotNull
    @Override
    public String getText() {
        return "Fix URL";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS XML";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return pkgXml.getURLs().size() > id;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        Pair<String, ROSPackageXml.URLType> url = pkgXml.getURLs().get(id);
        if (url.first.isEmpty()) {
            url = new Pair<>("http://example.com", ROSPackageXml.URLType.WEBSITE);
        } else if(url.second == null) {
            ROSPackageXml.URLType newUrl = ROSPackageXml.URLType.WEBSITE;
            if (url.first.contains("issue") || url.first.contains("bug")) {
                newUrl = ROSPackageXml.URLType.BUGTRACKER;
            }
            if (url.first.matches("https://github\\.com/[^/]+/[^/]+")) {
                newUrl = ROSPackageXml.URLType.REPOSITORY;
            }
            url = new Pair<>(url.first, newUrl);
        }
        pkgXml.setURL(id, url.first, url.second);
    }
}
