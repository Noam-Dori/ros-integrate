package ros.integrate.msg.psi.impl;

import com.google.common.primitives.UnsignedLong;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ros.integrate.msg.psi.ROSMsgConst;
import ros.integrate.msg.psi.ROSMsgField;

class ROSMsgFieldUtil {
    @Contract("null -> false")
    static boolean isLegalConstant(@NotNull ROSMsgField field) {
        ROSMsgConst msgConst = field.getConst();
        if (msgConst == null) { return false; }
        String num = msgConst.getText();
        String type = field.getType().raw().getText();
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
                    ret |= isNotBigger(integral, MaxValue.INT8) && i8;
                    ret |= isNotBigger(integral, MaxValue.UINT8) && ui8;
                    ret |= isNotBigger(integral, MaxValue.INT16) && i16;
                    ret |= isNotBigger(integral, MaxValue.UINT16) && ui16;
                    ret |= isNotBigger(integral, MaxValue.INT32) && i32;
                    ret |= isNotBigger(integral, MaxValue.UINT32) && ui32;
                    ret |= isNotBigger(integral, MaxValue.INT64) && i64;
                    ret |= ui64;
                }
                ret |= f32; // f32 is certainly in the range, but precision is damaged (doesnt matter in integrals)
            }
            return ret || f64 || str; // same reasoning as f32
        } catch (NumberFormatException e) {
            return str;
        }
    }



    private enum MaxValue {
        INT8(Byte.MAX_VALUE),
        UINT8((long) Byte.MAX_VALUE - Byte.MIN_VALUE),
        INT16(Short.MAX_VALUE),
        UINT16((long) Short.MAX_VALUE - Short.MIN_VALUE),
        INT32(Integer.MAX_VALUE),
        UINT32((long) Integer.MAX_VALUE - Integer.MIN_VALUE),
        INT64(Long.MAX_VALUE);

        @NotNull private final UnsignedLong value;

        MaxValue(long rawVal) {
            this.value = UnsignedLong.valueOf(rawVal);
        }

        @Contract(pure = true)
        public UnsignedLong get() {
            return value;
        }
    }

    private static boolean isNotBigger(@NotNull UnsignedLong unsignedLong, @NotNull MaxValue value) {
        return unsignedLong.compareTo(value.get()) <= 0;
    }
}
