package ros.integrate.msg.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.msg.psi.ROSMsgIdentifier;

public abstract class ROSMsgIdentifierImpl extends ASTWrapperPsiElement implements ROSMsgIdentifier {
    ROSMsgIdentifierImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return this;
    }

    @Override
    public PsiElement setName(@NotNull String name) {
        return set(name);
    }
}
