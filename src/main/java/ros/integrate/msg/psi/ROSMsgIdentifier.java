package ros.integrate.msg.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;

public interface ROSMsgIdentifier extends PsiNameIdentifierOwner {
    PsiElement set(String newName);
}
