package ros.integrate.pkg.xml.condition.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import ros.integrate.pkg.xml.condition.ROSConditionFileType;

public class ROSConditionElementFactory {
    public static ROSConditionToken createItem(Project project, String text) {
        return ((ROSCondition) PsiFileFactory.getInstance(project)
                .createFileFromText("dummy.condition", ROSConditionFileType.INSTANCE, text)).getTokens().get(0);
    }

    public static ROSConditionToken createLogic(Project project, String text) {
        return ((ROSCondition) PsiFileFactory.getInstance(project)
                .createFileFromText("dummy.condition", ROSConditionFileType.INSTANCE, "logic " + text + " logic"))
                .getTokens().get(1);
    }
}
