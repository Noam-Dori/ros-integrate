package ros.integrate.pkt.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;

/**
 * a generic inline identifier in ROS messages.
 */
public interface ROSPktIdentifier extends PsiNameIdentifierOwner {
    PsiElement set(String newName);
    @Override
    ROSPktFile getContainingFile(); // allows making it specifically a packet file

    ROSPktSection getContainingSection();
}
