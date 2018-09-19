package ros.integrate.msg.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.msg.ROSMsgTypeReference;
import ros.integrate.msg.psi.ROSMsgElementFactory;
import ros.integrate.msg.psi.ROSMsgType;
import ros.integrate.msg.psi.ROSMsgTypes;

import java.util.Objects;

class ROSMsgTypeUtil {
    @NotNull
    static PsiElement raw(@NotNull ROSMsgType type) {
        ASTNode keyNode = type.getNode().findChildByType(ROSMsgTypes.KEYTYPE);
        if (keyNode != null) {
            return keyNode.getPsi();
        } else {
            return Objects.requireNonNull(custom(type));
        }
    }

    @Nullable
    static PsiElement custom(@NotNull ROSMsgType type) {
        ASTNode keyNode = type.getNode().findChildByType(ROSMsgTypes.CUSTOM_TYPE);
        if (keyNode != null) {
            return keyNode.getPsi();
        } else {
            return null;
        }
    }

    static int size(@NotNull ROSMsgType element) {
        if (element.getNode().findChildByType(ROSMsgTypes.LBRACKET) != null) {
            ASTNode arrSize = element.getNode().findChildByType(ROSMsgTypes.NUMBER);
            if (arrSize != null) {
                return Integer.parseInt(arrSize.getText());
            }
            return 0;
        } else {
            return -1;
        }
    }

    @NotNull
    static PsiElement set(@NotNull ROSMsgType type, String rawType, int size) {
        String array = size == -1 ? "" : size == 0 ? "[]" : "[" + size + "]";
        if (type.getNode() != null && !type.getText().equals(rawType + array)) {
            ROSMsgType newType = ROSMsgElementFactory.createType(type.getProject(),rawType + array);
            type.replace(newType);
            return newType;
        }
        return type;
    }

    @NotNull
    @Contract("_, _ -> param1")
    static PsiElement set(@NotNull ROSMsgType type, String rawType) throws IncorrectOperationException {
        if(type.custom() == null) {
            throw new IncorrectOperationException("key-types cannot be refactored");
        }
        if (type.getNode() != null && !type.raw().getText().equals(rawType)) {
            ROSMsgType newType = ROSMsgElementFactory.createType(type.getProject(),rawType);
            type.raw().replace(newType.raw());
        }
        return type;
    }

    @Contract(pure = true)
    static String getName(@NotNull ROSMsgType type) {
        return type.raw().getText();
    }

    @Contract("_ -> param1")
    static PsiElement removeArray(@NotNull ROSMsgType type) {
        ASTNode lbr = type.getNode().findChildByType(ROSMsgTypes.LBRACKET);
        ASTNode rbr = type.getNode().findChildByType(ROSMsgTypes.RBRACKET);
        if (rbr != null && lbr != null) {
            type.deleteChildRange(lbr.getPsi(),rbr.getPsi()); // this also deletes whats inside the array.
        }
        return type;
    }

    @Nullable
    static PsiElement getNameIdentifier(@NotNull ROSMsgType type) {
        return type.custom();
    }

    @NotNull
    @Contract("_ -> new")
    static PsiReference getReference(@NotNull ROSMsgType type) {
        PsiElement raw = type.raw();
        int location = raw.getText().indexOf('/');
        TextRange range;
        if (location == -1) {
            range = new TextRange(0, raw.getText().length());
        } else {
            range = new TextRange(location, raw.getText().length());
        }
        return new ROSMsgTypeReference(type, range);
    }

    @NotNull
    static PsiReference[] getReferences(@NotNull ROSMsgType type) {
        return ReferenceProvidersRegistry.getReferencesFromProviders(type);
    }
}
