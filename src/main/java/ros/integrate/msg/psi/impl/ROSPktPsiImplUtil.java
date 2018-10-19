package ros.integrate.msg.psi.impl;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.ROSIcons;
import ros.integrate.msg.ROSMsgUtil;
import ros.integrate.msg.psi.*;

import javax.swing.*;

import static ros.integrate.msg.psi.ROSPktElementFactory.ANNOTATION_PREFIX;

/**
 * a utility class implementing all of the ROSMsg PSI objects' methods.
 * Also hold all documentation for them since it just passes them to the sub-utilities.
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
        if(ROSMsgUtil.checkAnnotation(comment) != null) {
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
    public static PsiElement raw(@NotNull ROSPktType type) {
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
    public static PsiElement custom(@NotNull ROSPktType type) {
        return ROSPktTypeUtil.custom(type);
    }

    /**
     * gets the array size of the field, if its even an array
     * @param type the element to test
     * @return -1 if the element is not an array,
     *         0 if the element has variable size (since size 0 should not be used)
     *         otherwise, the size of the array
     */
    public static int size(@NotNull ROSPktType type) {
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
    public static PsiElement set(@NotNull ROSPktType type, String rawType, int size) {
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
    public static PsiElement set(@NotNull ROSPktType type, String rawType) throws IncorrectOperationException {
        return ROSPktTypeUtil.set(type, rawType);
    }

    // utility function, do not use.
    @Contract(pure = true)
    public static String getName(@NotNull ROSPktType type) {
        return ROSPktTypeUtil.getName(type);
    }

    /**
     * removes the array part from the provided type if possible
     * @param type the type to change
     * @return type, but changed (or not)
     */
    @Contract("_ -> param1")
    public static PsiElement removeArray(@NotNull ROSPktType type) {
        return ROSPktTypeUtil.removeArray(type);
    }

    // utility function, do not use
    @Nullable
    public static PsiElement getNameIdentifier(@NotNull ROSPktType type) {
        return ROSPktTypeUtil.getNameIdentifier(type);
    }

    /**
     * implementation of {@link PsiElement#getReference()} for type psi-elements
     * @param type the type to getValue the reference of
     * @return the reference from this psi type to something else
     */
    @NotNull
    public static PsiReference getReference(@NotNull ROSPktType type) {
        return ROSPktTypeUtil.getReference(type);
    }

    /**
     * implementation of {@link PsiElement#getReferences()} for type psi-elements
     * @param type the type to getValue the reference of
     * @return the references from this psi type to something else
     */
    @NotNull
    public static PsiReference[] getReferences(@NotNull ROSPktType type) {
        return ROSPktTypeUtil.getReferences(type);
    }

    /**
     * changes the type provided, but not the array portion
     * @param label the field label to change
     * @param newName the new name used for the field
     * @return the new (or current) psi element put in place of the provided field label.
     */
    public static PsiElement set(@NotNull ROSPktLabel label, String newName) {
        return ROSPktLabelUtil.set(label, newName);
    }

    // utility function, do not use
    @Contract(pure = true)
    public static String getName(@NotNull ROSPktLabel label) {
        return ROSPktLabelUtil.getName(label);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static ItemPresentation getPresentation(final ROSPktField field) { return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return field.getType().getText();
            }

            @Override
            public String getLocationString() {
                return field.getContainingFile().getName();
            }

            @Override
            public Icon getIcon(boolean unused) {
                return ROSIcons.MsgFile;
            }
        };
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
    public static boolean isLegalConstant(@NotNull ROSPktField field) {
        return ROSPktFieldUtil.isLegalConstant(field);
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
}