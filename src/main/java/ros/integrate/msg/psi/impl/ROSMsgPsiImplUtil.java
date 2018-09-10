package ros.integrate.msg.psi.impl;

import com.google.common.primitives.UnsignedLong;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.ROSIcons;
import ros.integrate.msg.ROSMsgUtil;
import ros.integrate.msg.psi.*;

import javax.swing.*;
import java.util.Objects;

import static ros.integrate.msg.psi.ROSMsgElementFactory.ANNOTATION_PREFIX;

public class ROSMsgPsiImplUtil {
    @Nullable
    public static String getAnnotationIds(@NotNull ROSMsgComment comment) {
        if(ROSMsgUtil.checkAnnotation(comment) != null) {
            return comment.getText().substring(ANNOTATION_PREFIX.length());
        }
        return null;
    }

    @NotNull
    public static PsiElement raw(@NotNull ROSMsgType type) {
        ASTNode keyNode = type.getNode().findChildByType(ROSMsgTypes.KEYTYPE);
        if (keyNode != null) {
            return keyNode.getPsi();
        } else {
            return Objects.requireNonNull(custom(type));
        }
    }

    @Nullable
    public static PsiElement custom(@NotNull ROSMsgType type) {
        ASTNode keyNode = type.getNode().findChildByType(ROSMsgTypes.CUSTOM_TYPE);
        if (keyNode != null) {
            return keyNode.getPsi();
        } else {
            return null;
        }
    }

    /**
     * gets the array size of the field, if its even an array
     * @param element the element to test
     * @return -1 if the element is not an array,
     *         0 if the element has variable size (since size 0 should not be used)
     *         otherwise, the size of the array
     */
    public static int size(@NotNull ROSMsgType element) {
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

    public static PsiElement set(@NotNull ROSMsgType element, String rawType, int size) {
        String array = size == -1 ? "" : size == 0 ? "[]" : "[" + size + "]";
        if (element.getNode() != null && !element.getText().equals(rawType + array)) {
            ROSMsgType type = ROSMsgElementFactory.createType(element.getProject(),rawType + array);
            element.replace(type);
            return type;
        }
        return element;
    }

    @Contract("_, _ -> param1")
    public static PsiElement set(@NotNull ROSMsgType element, String rawType) throws IncorrectOperationException {
        if(element.custom() == null) {
            throw new IncorrectOperationException("key-types cannot be refactored");
        }
        if (element.getNode() != null && !element.raw().getText().equals(rawType)) {
            ROSMsgType type = ROSMsgElementFactory.createType(element.getProject(),rawType);
            element.raw().replace(type.raw());
        }
        return element;
    }

    public static PsiElement set(@NotNull ROSMsgLabel element, String newName) {
        if (element.getNode() != null && !element.getText().equals(newName)) {
            ROSMsgField field = ROSMsgElementFactory.createField(element.getProject(),"dummy " + newName);
            element.replace(field.getLabel());
            return field.getLabel();
        }
        return element;
    }

    @Contract(pure = true)
    public static String getName(@NotNull ROSMsgLabel element) {
        return element.getText();
    }

    @Contract(pure = true)
    public static String getName(@NotNull ROSMsgType element) {
        return element.raw().getText();
    }

    @Contract("_ -> param1")
    public static PsiElement removeArray(@NotNull ROSMsgType element) {
        ASTNode lbr = element.getNode().findChildByType(ROSMsgTypes.LBRACKET);
        ASTNode rbr = element.getNode().findChildByType(ROSMsgTypes.RBRACKET);
        if (rbr != null && lbr != null) {
            element.deleteChildRange(lbr.getPsi(),rbr.getPsi()); // this also deletes whats inside the array.
        }
        return element;
    }

    public static PsiElement getNameIdentifier(@NotNull ROSMsgType element) {
        return element.custom();
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static ItemPresentation getPresentation(final ROSMsgField element) { return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return element.getType().getText();
            }

            @Override
            public String getLocationString() {
                return element.getContainingFile().getName();
            }

            @Override
            public Icon getIcon(boolean unused) {
                return ROSIcons.MsgFile;
            }
        };
    }

    public static boolean isLegalConstant(@NotNull ROSMsgField element) {
        ROSMsgConst msgConst = element.getConst();
        if (msgConst == null) { return false; }
        String num = msgConst.getText();
        String type = element.getType().raw().getText();
        boolean f64 = "float64".equals(type),
                f32 = "float32".equals(type),
                i64 = "int64".equals(type),
                i32 = "int32".equals(type),
                i16 = "int16".equals(type),
                i8 = "int8".equals(type),
                ui64 = "uint64".equals(type),
                ui32 = "uint32".equals(type),
                ui16 = "uint16".equals(type),
                ui8 = "uint8".equals(type),
                str = "string".equals(type),
                ret = false;
        try {
            if (num.contains(".")) { // must be floating-point
                double floaty = Double.parseDouble(num);
                if ((double) (float) floaty == floaty) {
                    ret = f32;
                }
            } else { // integral
                if (num.contains("-")) { // must be int
                    long integral = Long.parseLong(num);
                    if ((long) (byte) integral == integral) {
                        ret = i8;
                    }
                    if ((long) (short) integral == integral) {
                        ret |= i16;
                    }
                    if ((long) (int) integral == integral) {
                        ret |= i32;
                    }
                    ret |= i64;
                } else { // uint
                    UnsignedLong integral = UnsignedLong.valueOf(num);
                    if (integral.byteValue() == 0 || integral.byteValue() == 1) {
                        ret = "bool".equals(type);
                    }
                    if (integral.compareTo(UnsignedLong.valueOf(Byte.MAX_VALUE)) <= 0) {
                        ret |= i8;
                    }
                    if (integral.compareTo(UnsignedLong.valueOf((long)Byte.MAX_VALUE - Byte.MIN_VALUE)) <= 0) {
                        ret |= ui8;
                    }
                    if (integral.compareTo(UnsignedLong.valueOf(Short.MAX_VALUE)) <= 0) {
                        ret |= i16;
                    }
                    if (integral.compareTo(UnsignedLong.valueOf((long)Short.MAX_VALUE - Short.MIN_VALUE)) <= 0) {
                        ret |= ui16;
                    }
                    if (integral.compareTo(UnsignedLong.valueOf(Integer.MAX_VALUE)) <= 0) {
                        ret |= i32;
                    }
                    if (integral.compareTo(UnsignedLong.valueOf((long)Integer.MAX_VALUE - Integer.MIN_VALUE)) <= 0) {
                        ret |= ui32;
                    }
                    if (integral.compareTo(UnsignedLong.valueOf(Long.MAX_VALUE)) <= 0) {
                        ret |= i64;
                    }
                    ret |= ui64;
                }
                ret |= f32; // f32 is certainly in the range, but precision is damaged (doesnt matter in integrals)
            }
            return ret || f64 || str; // same reasoning as f32
        } catch (NumberFormatException e) {
            return str;
        }
    }
}
