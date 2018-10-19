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
import ros.integrate.pkt.ROSPktTypeReference;
import ros.integrate.pkt.psi.ROSPktElementFactory;
import ros.integrate.pkt.psi.ROSPktType;
import ros.integrate.pkt.psi.ROSPktTypes;

import java.util.Objects;

/**
 * a utility class holding {@link ROSPktType} implementations
 */
class ROSPktTypeUtil {
    @NotNull
    static PsiElement raw(@NotNull ROSPktType type) {
        ASTNode keyNode = type.getNode().findChildByType(ROSPktTypes.KEYTYPE);
        if (keyNode != null) {
            return keyNode.getPsi();
        } else {
            return Objects.requireNonNull(custom(type));
        }
    }

    @Nullable
    static PsiElement custom(@NotNull ROSPktType type) {
        ASTNode keyNode = type.getNode().findChildByType(ROSPktTypes.CUSTOM_TYPE);
        if (keyNode != null) {
            return keyNode.getPsi();
        } else {
            return null;
        }
    }

    static int size(@NotNull ROSPktType element) {
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

    @NotNull
    static PsiElement set(@NotNull ROSPktType type, String rawType, int size) {
        String array = size == -1 ? "" : size == 0 ? "[]" : "[" + size + "]";
        if (type.getNode() != null && !type.getText().equals(rawType + array)) {
            ROSPktType newType = ROSPktElementFactory.createType(type.getProject(),rawType + array);
            type.replace(newType);
            return newType;
        }
        return type;
    }

    @NotNull
    @Contract("_, _ -> param1")
    static PsiElement set(@NotNull ROSPktType type, String rawType) throws IncorrectOperationException {
        if(type.custom() == null) {
            throw new IncorrectOperationException("key-types cannot be refactored");
        }
        if (type.getNode() != null && !type.raw().getText().equals(rawType)) {
            ROSPktType newType = ROSPktElementFactory.createType(type.getProject(),rawType);
            type.raw().replace(newType.raw());
        }
        return type;
    }

    @Contract(pure = true)
    static String getName(@NotNull ROSPktType type) {
        return type.raw().getText();
    }

    @Contract("_ -> param1")
    static PsiElement removeArray(@NotNull ROSPktType type) {
        ASTNode lbr = type.getNode().findChildByType(ROSPktTypes.LBRACKET);
        ASTNode rbr = type.getNode().findChildByType(ROSPktTypes.RBRACKET);
        if (rbr != null && lbr != null) {
            type.deleteChildRange(lbr.getPsi(),rbr.getPsi()); // this also deletes whats inside the array.
        }
        return type;
    }

    @Nullable
    static PsiElement getNameIdentifier(@NotNull ROSPktType type) {
        return type.custom();
    }

    @NotNull
    @Contract("_ -> new")
    static PsiReference getReference(@NotNull ROSPktType type) {
        PsiElement raw = type.raw();
        int location = raw.getText().indexOf('/');
        TextRange range;
        if (location == -1) {
            range = new TextRange(0, raw.getText().length());
        } else {
            range = new TextRange(location, raw.getText().length());
        }
        return new ROSPktTypeReference(type, range);
    }

    @NotNull
    static PsiReference[] getReferences(@NotNull ROSPktType type) {
        return ReferenceProvidersRegistry.getReferencesFromProviders(type);
    }
}
