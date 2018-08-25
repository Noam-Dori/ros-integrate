package ros.integrate.msg.psi.impl;

import com.google.common.primitives.UnsignedLong;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Contract;
import ros.integrate.ROSIcons;
import ros.integrate.msg.psi.ROSMsgConst;
import ros.integrate.msg.psi.ROSMsgElementFactory;
import ros.integrate.msg.psi.ROSMsgProperty;
import ros.integrate.msg.psi.ROSMsgTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

public class ROSMsgPsiImplUtil {
    @Nullable
    public static String getCustomType(@NotNull ROSMsgProperty element) {
        ASTNode keyNode = element.getType().getNode().findChildByType(ROSMsgTypes.CUSTOM_TYPE);
        if (keyNode != null) {
            return keyNode.getText();
        } else {
            return null;
        }
    }

    @Nullable
    public static String getRawType(@NotNull ROSMsgProperty element) {
        ASTNode keyNode = element.getType().getNode().findChildByType(ROSMsgTypes.KEYTYPE);
        if (keyNode != null) {
            return keyNode.getText();
        } else {
            return getCustomType(element);
        }
    }

    /**
     * gets the array size of the property, if its even an array
     * @param element the element to test
     * @return -1 if the element is not an array,
     *         0 if the element has variable size (since size 0 should not be used)
     *         otherwise, the size of the array
     */
    public static int getArraySize(@NotNull ROSMsgProperty element) {
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

    @Contract("_, _ -> param1")
    public static PsiElement setType(@NotNull ROSMsgProperty element, String newName) {
        ASTNode typeNode = element.getNode().findChildByType(ROSMsgTypes.TYPE);
        if (typeNode != null) {

            ROSMsgProperty property = ROSMsgElementFactory.createProperty(element.getProject(), newName);
            ASTNode newTypeNode = property.getFirstChild().getNode();
            element.getNode().replaceChild(typeNode, newTypeNode);
        }
        return element;
    }

    @Contract("_, _ -> param1")
    public static PsiElement setFieldName(@NotNull ROSMsgProperty element, String newName) {
        ASTNode typeNode = element.getNode().findChildByType(ROSMsgTypes.NAME);
        if (typeNode != null) {

            ROSMsgProperty property = ROSMsgElementFactory.createProperty(element.getProject(),"dummy " + newName);
            ASTNode newTypeNode = Objects.requireNonNull(property.getNode().findChildByType(ROSMsgTypes.NAME));
            element.getNode().replaceChild(typeNode, newTypeNode);
        }
        return element;
    }

    @Contract("_ -> param1")
    public static PsiElement removeArray(@NotNull ROSMsgProperty element) {
        ASTNode lbr = element.getNode().findChildByType(ROSMsgTypes.LBRACKET);
        ASTNode rbr = element.getNode().findChildByType(ROSMsgTypes.RBRACKET);
        if (rbr != null && lbr != null) {
            element.deleteChildRange(lbr.getPsi(),rbr.getPsi()); // this also deletes whats inside the array.
        }
        return element;
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static ItemPresentation getPresentation(final ROSMsgProperty element) { return new ItemPresentation() {
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
                return ROSIcons.MSG_FILE;
            }
        };
    }

    public static boolean canHandle(@NotNull ROSMsgProperty element, @NotNull ROSMsgConst msgConst) {
        String num = msgConst.getText();
        String type = element.getRawType();
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
