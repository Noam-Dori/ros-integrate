package ros.integrate.pkt.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.ROSPktUtil;
import ros.integrate.pkt.psi.*;

import java.util.List;

import static ros.integrate.pkt.psi.ROSPktElementFactory.ANNOTATION_PREFIX;

/**
 * a utility class implementing all of the ROSMsg PSI objects' methods.
 * Also hold all documentation for them since it just passes them to the sub-utilities.
 * @author Noam Dori
 */
public class ROSPktPsiImplUtil {
    /**
     * fetches all annotation IDS if any
     * @param comment the comment to search (this)
     * @return <code>""</code> if the provided comment has no suppression annotation,
     *         otherwise a string of IDS, concatenated with commas.
     */
    @Nullable
    public static String getAnnotationIds(@NotNull ROSPktComment comment) {
        if(ROSPktUtil.checkAnnotation(comment, null) != null) {
            return comment.getText().substring(ANNOTATION_PREFIX.length());
        }
        return null;
    }

    /**
     * gets the raw portion of the type
     * @param type the type to search (this)
     * @return every type must have a raw section, so it is never null.
     *         if the type is a key-type, returns the key-type psi element containing the text of the type.
     *         if the type is a custom type, returns the psi element containing that custom type.
     *         otherwise, throws a null-check exception.
     *         the array portion is never included in the returned psi element.
     */
    @NotNull
    static PsiElement raw(@NotNull ROSPktTypeBase type) {
        return ROSPktTypeUtil.raw(type);
    }

    /**
     * gets the raw portion of the type, but only if it is a custom type (not a key-type)
     * @param type the type to search (this)
     * @return every type must have a raw section, so it is never null.
     *         if the type is a custom type, returns the psi element containing that custom type.
     *         otherwise, returns null
     *         the array portion is never included in the returned psi element.
     */
    @Nullable
    static PsiElement custom(@NotNull ROSPktTypeBase type) {
        return ROSPktTypeUtil.custom(type);
    }

    /**
     * gets the array size of the field, if its even an array
     * @param type the element to test
     * @return -1 if the element is not an array,
     *         0 if the element has variable size (since size 0 should not be used)
     *         otherwise, the size of the array
     */
    static int size(@NotNull ROSPktTypeBase type) {
        return ROSPktTypeUtil.size(type);
    }

    /**
     * changes the type provided
     * @param type the type to change
     * @param rawType the new raw type used
     * @param size the new size of the array:
     *                      -1 if the element is not an array or should no longer be one,
     *                      0 if the element has variable size (since size 0 should not be used)
     *                      otherwise, the size of the array
     * @return the new (or current) psi element put in place of the provided type.
     */
    @NotNull
    static PsiElement set(@NotNull ROSPktTypeBase type, String rawType, int size) {
        return ROSPktTypeUtil.set(type, rawType, size);
    }

    /**
     * changes the type provided, but not the array portion
     * @param type the type to change
     * @param rawType the new raw type used
     * @return type, but changed.
     */
    @NotNull
    @Contract("_, _ -> param1")
    static PsiElement set(@NotNull ROSPktTypeBase type, String rawType) throws IncorrectOperationException {
        return ROSPktTypeUtil.set(type, rawType);
    }

    /**
     * Returns the name of the element.
     * @param type the field type to check
     * @return the name of the element
     * @apiNote utility function, do not use
     */
    @Contract(pure = true)
    static String getName(@NotNull ROSPktTypeBase type) {
        return ROSPktTypeUtil.getName(type);
    }

    /**
     * removes the array part from the provided type if possible
     * @param type the type to change
     * @return type, but changed (or not)
     */
    @NotNull
    @Contract("_ -> param1")
    static PsiElement removeArray(@NotNull ROSPktTypeBase type) {
        return ROSPktTypeUtil.removeArray(type);
    }

    /**
     * Returns the name identifier of the element.
     * @param type the field type to check
     * @return the name of the element
     * @apiNote utility function, do not use
     */
    @Nullable
    static PsiElement getNameIdentifier(@NotNull ROSPktTypeBase type) {
        return ROSPktTypeUtil.getNameIdentifier(type);
    }

    /**
     * implementation of {@link PsiElement#getReference()} for type psi-elements
     * @param type the type to getValue the reference of
     * @return the reference from this psi type to something else
     */
    @NotNull
    static PsiReference getReference(@NotNull ROSPktTypeBase type) {
        return ROSPktTypeUtil.getReference(type);
    }

    /**
     * implementation of {@link PsiElement#getReferences()} for type psi-elements
     * @param type the type to getValue the reference of
     * @return the references from this psi type to something else
     */
    @NotNull
    static PsiReference[] getReferences(@NotNull ROSPktTypeBase type) {
        return ROSPktTypeUtil.getReferences(type);
    }

    /**
     * checks if this field type is a complete field type (which it is)
     * @param type the field/field fragment to use
     * @return true if this field type is NOT a fragment and follows all rules of packet field types, false otherwise
     * @apiNote utility function, do not use
     */
    @Contract(pure = true)
    static boolean isComplete(@NotNull ROSPktType type) {
        return ROSPktTypeUtil.isComplete(type);
    }

    /**
     * changes the type provided, but not the array portion
     * @param label the field label to change
     * @param newName the new name used for the field
     * @return the new (or current) psi element put in place of the provided field label.
     */
    @NotNull
    @Contract("_, _ -> param1")
    public static PsiElement set(@NotNull ROSPktLabel label, String newName) {
        return ROSPktLabelUtil.set(label, newName);
    }

    /**
     * Returns the name of the element.
     * @param label the field label to check
     * @return the name of the element
     * @apiNote utility function, do not use
     */
    @Contract(pure = true)
    public static String getName(@NotNull ROSPktLabel label) {
        return ROSPktLabelUtil.getName(label);
    }

    /**
     * checks whether or not this field is a sufficient constant,
     * that is, it can contain the numerical value provided with the given memory it is permitted to use,
     * and is properly formatted to be properly kept.
     * @param field the field to check.
     * @return false if one of the following is true:
     *              - the field is NOT a constant field
     *              - the value within the field cannot be contained within the type provided in it.
     *         otherwise, returns true.
     */
    @Contract("null -> false")
    static boolean isLegalConstant(@NotNull ROSPktFieldBase field) {
        return ROSPktFieldUtil.isLegalConstant(field);
    }

    /**
     * fetches the base type of this field, even if it is a fragment or the field itself is a fragment.
     * @param field the field/field fragment to use
     * @return a non-null base type
     */
    @NotNull
    static ROSPktType getTypeBase(@NotNull ROSPktField field) {
        return ROSPktFieldUtil.getTypeBase(field);
    }

    /**
     * @see ROSPktPsiImplUtil#getTypeBase(ROSPktField)
     * this is an individual implementation for field fragments which can return either a complete type, or a fragment.
     */
    @NotNull
    static ROSPktTypeBase getTypeBase(@NotNull ROSPktFieldFrag field) {
        return ROSPktFieldUtil.getTypeBase(field);
    }

    /**
     * checks if this field is a complete field (which it is)
     * @param field the field/field fragment to use
     * @return true if this field is NOT a fragment and follows all rules of packet fields, false otherwise
     */
    @Contract(pure = true)
    static boolean isComplete(@NotNull ROSPktField field) {
        return ROSPktFieldUtil.isComplete(field);
    }

    /**
     * provided that this field is a constant holding field, find the optimal data-type to hold the constant within it.
     * @param constant the constant to optimise
     * @return <code>null</code> iff the field is not a constant,
     * otherwise a non-empty key-type holding the best data-type to use for the constant with respect to memory and actual size.
     */
    @NotNull
    public static ROSPktType getBestFit(@NotNull ROSPktConst constant) {
        return ROSPktFieldUtil.getBestFit(constant);
    }

    /**
     * gets all available (and valid) fields in this section.
     * @return a list of all available fields in this section in textual order.
     * @param section the section to search for fields
     * @param queryClass the class of which to search. If limited to complete fields, use {@link ROSPktField}
     *                   if fragments need be searched use {@link ROSPktFieldFrag}.
     *                   if you want both, use {@link ROSPktFieldBase}
     * @param includeConstants whether or not constant fields should be included
     */
    @NotNull
    public static <T extends ROSPktFieldBase> List<T> getFields(@NotNull ROSPktSection section, Class<T> queryClass,
                                                                boolean includeConstants) {
        return ROSPktSectionUtil.getFields(section, queryClass, includeConstants);
    }
}