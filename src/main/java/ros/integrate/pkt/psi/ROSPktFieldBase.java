package ros.integrate.pkt.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * a packet field. This is one line in a packet files (.msg, .srv, .action).
 * It describes either one property within the packet or one constant value that can be used
 *
 * This specific class describes every field line, complete or broken. The line can have missing parts.
 * If the line completely follows the rules, it is considered "complete", if not, it is considered a "fragment"
 * You can still so some analytics on fragment fields. They do have types for sure and optionally other things.
 * Use {@link ROSPktFieldBase#isComplete()} to check whether or not the field is complete.
 * @author Noam Dori
 */
public interface ROSPktFieldBase extends PsiElement {
    /**
     * try to get the const value attached to this field
     * @return the PSI pointer to the constant value if one exists in this field, otherwise, null.
     * This can happen in these scenarios:
     * <ul>
     *     <li>the field (or fragment) is not a const-value field and instead a property field</li>
     *     <li>the field is a fragment and forgot to add the const value</li>
     * </ul>
     */
    @Nullable
    ROSPktConst getConst();

    /**
     * try to get the name used to reference this field either to get the data it holds or to assign data to it
     * @return the PSI pointer to the element holding the name of the field. If it does not exist, returns null.
     * This can happen in these scenarios:
     * <ul>
     *     <li>the field is a fragment for forgot to the name</li>
     * </ul>
     * complete fields MUST have a label, and even some fragments have labels.
     */
    @Nullable
    ROSPktLabel getLabel();

    /**
     * get the type of data this field has
     * @return the PSI pointer to the element holding the name of the field.
     * Every field, fragment or complete, has a type. However, that type itself does not have to be complete.
     * Thus, this will never return null
     */
    @NotNull
    ROSPktTypeBase getTypeBase();

    /**
     * checks whether or not this field is a sufficient constant,
     * that is, it can contain the numerical value provided with the given memory it is permitted to use,
     * and is properly formatted to be properly kept.
     * @return false if one of the following is true:
     *         - the field is NOT a constant field
     *         - the value within the field cannot be contained within the type provided in it.
     *         otherwise, returns true.
     */
    boolean isLegalConstant();

    /**
     * whether or not the field is complete, or is some sort of fragment.
     * @return true is it is considered a valid type, false otherwise.
     */
    boolean isComplete();

    @Override
    ROSPktFile getContainingFile();
}
