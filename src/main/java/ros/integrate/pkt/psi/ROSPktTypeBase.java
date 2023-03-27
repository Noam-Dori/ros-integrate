package ros.integrate.pkt.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * a packet field type. This is a type identifier for a specific field in a packet file (.msg, .srv, .action).
 * It describes the structure of the data in the field (constant or property).
 * <p>
 * This specific class describes every field type. The type can be complete or broken.
 * The very string describing the type can have missing parts.
 * If the type completely follows the rules, it is considered "complete", if not, it is considered a "fragment".
 * Types are fragments only if they have a left bracket but not a right one (this is the only way to really break them)
 * You can run all functions on fragment fields as if they were complete and expect similar results,
 * at least when it comes to the logical part.
 * Use {@link ROSPktTypeBase#isComplete()} to check whether the type is complete.
 * @author Noam Dori
 */
public interface ROSPktTypeBase extends ROSPktIdentifier {
    /**
     * gets the raw portion of the type
     * @return every type must have a raw section, so it is never null.
     *         if the type is a key-type, returns the key-type psi element containing the text of the type.
     *         if the type is a custom type, returns the psi element containing that custom type.
     *         otherwise, throws a null-check exception.
     *         the array portion is never included in the returned psi element.
     */
    @NotNull
    PsiElement raw();

    /**
     * gets the raw portion of the type, but only if it is a custom type (not a key-type)
     * @return every type must have a raw section, so it is never null.
     *         if the type is a custom type, returns the psi element containing that custom type.
     *         otherwise, returns null
     *         the array portion is never included in the returned psi element.
     */
    @Nullable
    PsiElement custom();

    /**
     * gets the array size of the type, even if it's not an array
     * @return -1 if the element is not an array,
     *         0 if the element has variable size (since size 0 should not be used)
     *         otherwise, the size of the array
     */
    int size();

    /**
     * removes the array part from the provided type if possible
     * @return type, but changed (or not)
     */
    @SuppressWarnings("UnusedReturnValue")
    PsiElement removeArray();

    /**
     * changes the type
     * @param rawType the new raw type used
     * @param size the new size of the array:
     *                      -1 if the element is not an array or should no longer be one,
     *                      0 if the element has variable size (since size 0 should not be used)
     *                      otherwise, the size of the array
     * @return the new (or current) psi element put in place of the provided type.
     */
    @SuppressWarnings("UnusedReturnValue")
    @NotNull
    PsiElement set(String rawType, int size);

    /**
     * changes the type, but not the array portion
     * @param rawType the new raw type used
     * @return type, but changed.
     */
    @NotNull
    PsiElement set(String rawType);

    String getName();

    @Nullable
    PsiElement getNameIdentifier();

    @NotNull
    PsiReference getReference();

    PsiReference @NotNull [] getReferences();

    /**
     * whether the type is complete, or is some sort of fragment.
     * @return true is it is considered a valid type, false otherwise.
     */
    boolean isComplete();
}
