package ros.integrate.pkg.xml.condition.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.condition.ROSConditionFileType;

/**
 * a utility class used to construct tokens within ROS conditions
 * @author Noam Dori
 */
public class ROSConditionElementFactory {
    /**
     * construct a new verb, either a VARIABLE or a LITERAL
     * @param project the project this token belongs to
     * @param text the text that is used as template for the PSI token
     * @return if the text starts with a $, a new VARIABLE with the text as input,
     *         otherwise a new LITERAL with the text as input.
     */
    public static ROSConditionToken createItem(Project project, String text) {
        return ((ROSCondition) PsiFileFactory.getInstance(project)
                .createFileFromText("dummy.condition", ROSConditionFileType.INSTANCE, text)).getTokens().get(0);
    }

    /**
     * construct a new logic token, a token used to represent the relationship between expressions
     * @param project the project this token belongs to
     * @param text the text that is used as template for the PSI token
     * @return a potential logic token. If the text is not ==,<,>,<=,>=,!=,and,or it will not be a logic item
     */
    public static ROSConditionToken createLogic(Project project, String text) {
        return ((ROSCondition) PsiFileFactory.getInstance(project)
                .createFileFromText("dummy.condition", ROSConditionFileType.INSTANCE, "logic " + text + " logic"))
                .getTokens().get(1);
    }

    /**
     * construct an entire ROS condition
     * @param project the project this token belongs to
     * @param text the text that is used as template for the PSI token
     * @return a ROS condition with the text as input.
     */
    @NotNull
    public static ROSCondition createCondition(Project project, String text) {
        return ((ROSCondition) PsiFileFactory.getInstance(project)
                .createFileFromText("dummy.condition", ROSConditionFileType.INSTANCE, text));
    }
}
