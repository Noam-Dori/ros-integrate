package ros.integrate.pkt.psi.impl;

import com.google.common.primitives.UnsignedLong;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.psi.*;

import java.util.Objects;

/**
 * a utility class holding {@link ROSPktFieldBase} implementations
 * @author Noam Dori
 */
class ROSPktFieldUtil {

    /**
     * fetches the base type of this field, even if it is a fragment or the field itself is a fragment.
     * @param field the field fragment to use
     * @return a non-null base type
     */
    @NotNull
    static ROSPktTypeBase getTypeBase(@NotNull ROSPktFieldFrag field) {
        ROSPktType type = field.getType();
        return type != null ? type : Objects.requireNonNull(field.getTypeFrag());
    }

    /**
     * fetches the base type of this field, even if it is a fragment or the field itself is a fragment.
     * @param field the field to use
     * @return a non-null base type
     */
    @NotNull
    static ROSPktType getTypeBase(@NotNull ROSPktField field) {
        return field.getType();
    }

    /**
     * checks whether this field is a sufficient constant,
     * that is, it can contain the numerical value provided with the given memory it is permitted to use,
     * and is properly formatted to be properly kept.
     * @param field the field to check.
     * @return false if one of the following is true:
     *              - the field is NOT a constant field
     *              - the value within the field cannot be contained within the type provided in it.
     *         otherwise, returns true.
     */
    static boolean isLegalConstant(@NotNull ROSPktFieldBase field) {
        ROSPktConst msgConst = field.getConst();
        if (msgConst == null) { return false; }
        String num = msgConst.getText();
        String type = field.getTypeBase().raw().getText();
        boolean f64 = "float64".equals(type),
                f32 = "float32".equals(type),
                i64 = MaxValue.INT64.nameEquals(type),
                i32 = MaxValue.INT32.nameEquals(type),
                i16 = MaxValue.INT16.nameEquals(type),
                i8 = MaxValue.INT8.nameEquals(type) || "byte".equals(type),
                ui64 = "uint64".equals(type),
                ui32 = MaxValue.UINT32.nameEquals(type),
                ui16 = MaxValue.UINT16.nameEquals(type),
                ui8 = MaxValue.UINT8.nameEquals(type) || "char".equals(type),
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
                    if (isNotBigger(integral, MaxValue.BIT)) {
                        ret = MaxValue.BIT.nameEquals(type);
                    }
                    ret |= isNotBigger(integral, MaxValue.INT8) && i8;
                    ret |= isNotBigger(integral, MaxValue.UINT8) && ui8;
                    ret |= isNotBigger(integral, MaxValue.INT16) && i16;
                    ret |= isNotBigger(integral, MaxValue.UINT16) && ui16;
                    ret |= isNotBigger(integral, MaxValue.INT32) && i32;
                    ret |= isNotBigger(integral, MaxValue.UINT32) && ui32;
                    ret |= isNotBigger(integral, MaxValue.INT64) && i64;
                    ret |= ui64;
                }
                ret |= f32; // f32 is certainly in the range, but precision is damaged (doesn't matter in integrals)
            }
            return ret || f64 || str; // same reasoning as f32
        } catch (NumberFormatException e) {
            return str;
        }
    }

    /**
     * provided that this field is a constant holding field, find the optimal data-type to hold the constant within it.
     * @param constant the constant to optimise
     * @return <code>null</code> iff the field is not a constant,
     * otherwise a non-empty key-type holding the best data-type to use for the constant with respect to memory and actual size.
     */
    @NotNull
    static ROSPktType getBestFit(@NotNull ROSPktConst constant) {
        String num = constant.getText();
        Project project = constant.getProject();
        try {
            if (num.contains(".")) { // floating-point
                double floaty = Double.parseDouble(num);
                if ((double) (float) floaty == floaty) {
                    return ROSPktElementFactory.createType(project, "float32");
                } else {
                    return ROSPktElementFactory.createType(project, "float64");
                }
            } else { // integral
                if (num.contains("-")) { // int
                    long integral = Long.parseLong(num);
                    if ((long) (byte) integral == integral) {
                        return MaxValue.INT8.createType(project);
                    }
                    if ((long) (short) integral == integral) {
                        return MaxValue.INT16.createType(project);
                    }
                    if ((long) (int) integral == integral) {
                        return MaxValue.INT32.createType(project);
                    }
                    return MaxValue.INT64.createType(project);
                } else { // uint
                    UnsignedLong integral = UnsignedLong.valueOf(num);
                    if (isNotBigger(integral, MaxValue.BIT)) {
                        return MaxValue.BIT.createType(project);
                    }
                    if (isNotBigger(integral, MaxValue.UINT8)) {
                        return MaxValue.UINT8.createType(project);
                    }
                    if (isNotBigger(integral, MaxValue.UINT16)) {
                        return MaxValue.UINT16.createType(project);
                    }
                    if (isNotBigger(integral, MaxValue.UINT32)) {
                        return MaxValue.UINT32.createType(project);
                    }
                    return ROSPktElementFactory.createType(project, "uint64");
                }
            }
        } catch (NumberFormatException e) {
            return ROSPktElementFactory.createType(project,"string");
        }
    }

    /**
     * an internal definition of the numerical limits of the builtin data-types
     */
    private enum MaxValue {
        BIT(1,"bool"),
        INT8(Byte.MAX_VALUE, "int8"),
        UINT8((long) Byte.MAX_VALUE - Byte.MIN_VALUE, "uint8"),
        INT16(Short.MAX_VALUE, "int16"),
        UINT16((long) Short.MAX_VALUE - Short.MIN_VALUE, "uint16"),
        INT32(Integer.MAX_VALUE, "int32"),
        UINT32((long) Integer.MAX_VALUE - Integer.MIN_VALUE, "uint32"),
        INT64(Long.MAX_VALUE, "int64");

        @NotNull private final UnsignedLong value;
        @NotNull private final String name;

        /**
         * construct a new numerical limits entity
         * @param rawVal the actual number that is the maximum
         * @param typeName the name of the type this number represents the max value for
         */
        MaxValue(long rawVal, @NotNull String typeName) {
            this.value = UnsignedLong.valueOf(rawVal);
            this.name = typeName;
        }

        /**
         * @return the actual max value
         */
        @NotNull
        @Contract(pure = true)
        UnsignedLong getValue() {
            return value;
        }

        /**
         * creates a PSI field type of this data type
         * @param project the project this type belongs to
         * @return a new PSI field type with the builtin data type as argument
         */
        @NotNull
        @Contract(pure = true)
        ROSPktType createType(@NotNull Project project) {
            return ROSPktElementFactory.createType(project, name);
        }

        /**
         * checks if the name of this data type is the name as the input string
         * @param otherName the string to compare against
         * @return true if the datatype name is the same as the input, false otherwise
         */
        @Contract(value = "null -> false", pure = true)
        boolean nameEquals(@Nullable String otherName) {
            return name.equals(otherName);
        }
    }

    private static boolean isNotBigger(@NotNull UnsignedLong unsignedLong, @NotNull MaxValue value) {
        return unsignedLong.compareTo(value.getValue()) <= 0;
    }

    /**
     * checks if this field is a complete field (which it is)
     * @param field the field/field fragment to use
     * @return true if this field is NOT a fragment and follows all rules of packet fields, false otherwise
     */
    @SuppressWarnings("SameReturnValue")
    @Contract(pure = true)
    static boolean isComplete(@SuppressWarnings({"unused", "RedundantSuppression"}) @NotNull ROSPktField field) {
        return true;
    }
}
