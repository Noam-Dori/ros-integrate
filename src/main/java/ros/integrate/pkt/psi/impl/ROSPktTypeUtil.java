package ros.integrate.pkt.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.ROSMsgFileReference;
import ros.integrate.pkt.psi.ROSPktElementFactory;
import ros.integrate.pkt.psi.ROSPktType;
import ros.integrate.pkt.psi.ROSPktTypeBase;
import ros.integrate.pkt.psi.ROSPktTypes;

import java.util.Objects;

/**
 * a utility class holding {@link ROSPktTypeBase} implementations
 * @author Noam Dori
 */
class ROSPktTypeUtil {
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
        ASTNode keyNode = type.getNode().findChildByType(ROSPktTypes.KEYTYPE);
        if (keyNode != null) {
            return keyNode.getPsi();
        } else {
            keyNode = type.getFirstChild().getNode().findChildByType(ROSPktTypes.KEYTYPE);
            if (keyNode != null) {
                return keyNode.getPsi();
            } else {
                return Objects.requireNonNull(type.custom());
            }
        }
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
        ASTNode keyNode = type.getNode().findChildByType(ROSPktTypes.CUSTOM_TYPE);
        if (keyNode != null) {
            return keyNode.getPsi();
        } else {
            keyNode = type.getFirstChild().getNode().findChildByType(ROSPktTypes.CUSTOM_TYPE);
            if (keyNode != null) {
                return keyNode.getPsi();
            } else {
                return null;
            }
        }
    }

    /**
     * gets the array size of the field, if its even an array
     * @param element the element to test
     * @return -1 if the element is not an array,
     *         0 if the element has variable size (since size 0 should not be used)
     *         otherwise, the size of the array
     */
    static int size(@NotNull ROSPktTypeBase element) {
        if (element.getNode().findChildByType(ROSPktTypes.LBRACKET) != null) {
            ASTNode arrSize = element.getNode().findChildByType(ROSPktTypes.NUMBER);
            if (arrSize != null) {
                return Integer.parseInt(arrSize.getText());
            }
            return 0;
        } else {
            return -1;
        }
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
        String array = size == -1 ? "" : size == 0 ? "[]" : "[" + size + "]";
        if (type.getNode() != null && !type.getText().equals(rawType + array)) {
            ROSPktType newType = ROSPktElementFactory.createType(type.getProject(),rawType + array);
            type.replace(newType);
            return newType;
        }
        return type;
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
        if(type.custom() == null) {
            throw new IncorrectOperationException("key-types cannot be refactored");
        }
        if (type.getNode() != null && !type.raw().getText().equals(rawType)) {
            ROSPktType newType = ROSPktElementFactory.createType(type.getProject(),rawType);
            type.raw().replace(newType.raw());
        }
        return type;
    }

    /**
     * Returns the name of the element.
     * @param type the field type to check
     * @return the name of the element
     * @apiNote utility function, do not use
     */
    @Contract(pure = true)
    static String getName(@NotNull ROSPktTypeBase type) {
        return type.raw().getText();
    }

    /**
     * removes the array part from the provided type if possible
     * @param type the type to change
     * @return type, but changed (or not)
     */
    @NotNull
    @Contract("_ -> param1")
    static PsiElement removeArray(@NotNull ROSPktTypeBase type) {
        ASTNode lbr = type.getNode().findChildByType(ROSPktTypes.LBRACKET);
        ASTNode rbr = type.getNode().findChildByType(ROSPktTypes.RBRACKET);
        if (rbr != null && lbr != null) {
            type.deleteChildRange(lbr.getPsi(),rbr.getPsi()); // this also deletes what's inside the array.
        } else { // delete the array from the fragment
            ASTNode numeral = type.getNode().findChildByType(ROSPktTypes.NUMBER);
            if (rbr != null) {
                type.deleteChildRange(rbr.getPsi(),rbr.getPsi());
            }
            if (numeral != null) {
                type.deleteChildRange(numeral.getPsi(),numeral.getPsi());
            }
            if (lbr != null) {
                type.deleteChildRange(lbr.getPsi(),lbr.getPsi());
            }
        }
        return type;
    }

    /**
     * Returns the name identifier of the element.
     * @param type the field type to check
     * @return the name of the element
     * @apiNote utility function, do not use
     */
    @SuppressWarnings("SameReturnValue")
    @Contract(pure = true)
    @Nullable
    static PsiElement getNameIdentifier(@SuppressWarnings({"unused", "RedundantSuppression"}) @NotNull ROSPktTypeBase type) {
        return null;
    }

    /**
     * implementation of {@link PsiElement#getReference()} for type psi-elements
     * @param type the type to getValue the reference of
     * @return the reference from this psi type to something else
     */
    @NotNull
    @Contract("_ -> new")
    static PsiReference getReference(@NotNull ROSPktTypeBase type) {
        PsiElement raw = type.raw();
        int location = raw.getText().indexOf('/');
        TextRange range = new TextRange(location + 1, raw.getText().length());
        return new ROSMsgFileReference(type, range);
    }

    /**
     * implementation of {@link PsiElement#getReferences()} for type psi-elements
     * @param type the type to getValue the reference of
     * @return the references from this psi type to something else
     */
    static PsiReference @NotNull [] getReferences(@NotNull ROSPktTypeBase type) {
        return ReferenceProvidersRegistry.getReferencesFromProviders(type);
    }

    /**
     * checks if this field type is a complete field type (which it is)
     * @param type the field/field fragment to use
     * @return true if this field type is NOT a fragment and follows all rules of packet field types, false otherwise
     * @apiNote utility function, do not use
     */
    @SuppressWarnings("SameReturnValue")
    @Contract(pure = true)
    static boolean isComplete(@SuppressWarnings({"unused", "RedundantSuppression"}) @NotNull ROSPktType type) {
        return true;
    }
}
