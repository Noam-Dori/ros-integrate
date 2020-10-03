package ros.integrate.pkt.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;

/**
 * a base interface for identifiable named elements in packet files these are the leaf "string" elements
 * @author Noam Dori
 */
public interface ROSPktIdentifier extends PsiNameIdentifierOwner {
    /**
     * changes the identifying part (the string) of the element
     * @param newName the new text to put in the identifying part
     * @return this element with the modification applied
     */
    PsiElement set(String newName);
    @Override
    ROSPktFile getContainingFile(); // allows making it specifically a packet file

    /**
     * @return the section (a collection of fields that are not delimited by a separator (---)) this element belongs to
     */
    ROSPktSection getContainingSection();
}
