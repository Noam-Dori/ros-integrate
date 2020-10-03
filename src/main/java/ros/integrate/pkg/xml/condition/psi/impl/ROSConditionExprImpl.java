package ros.integrate.pkg.xml.condition.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.condition.psi.ROSConditionExpr;
import ros.integrate.pkg.xml.condition.psi.ROSConditionToken;

import java.util.List;

/**
 * base implementation of a ROS condition expression
 */
public abstract class ROSConditionExprImpl extends ASTWrapperPsiElement implements ROSConditionExpr {

    /**
     * construct a new expression
     * @param node the corresponding AST node
     */
    public ROSConditionExprImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    public List<ROSConditionToken> getTokens() {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, ROSConditionToken.class);
    }
}
