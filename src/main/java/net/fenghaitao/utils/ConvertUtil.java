package net.fenghaitao.utils;

import net.fenghaitao.exception.AutoExcelException;
import org.apache.poi.ss.usermodel.DateUtil;

import java.math.BigDecimal;

public class ConvertUtil {
    public static Object convert(Class targetClass, Object value) {
        if (value == null || targetClass.isInstance(value))
            return value;

        switch (targetClass.getName()) {
            case "java.lang.String":
                return value.toString();
            case "java.util.Date":
                return DateUtil.getJavaDate(Double.parseDouble(value.toString()));
            case "java.math.BigDecimal":
                return new BigDecimal(value.toString());
            case "java.lang.Double":
            case "double":
                if (value instanceof BigDecimal)
                    return ((BigDecimal) value).doubleValue();
                else
                    return Double.valueOf(value.toString());
            case "java.lang.Float":
            case "float":
                if (value instanceof BigDecimal)
                    return ((BigDecimal) value).floatValue();
                else
                    return Float.valueOf(value.toString());
            case "java.lang.Long":
            case "long":
                if (value instanceof BigDecimal)
                    return ((BigDecimal) value).longValue();
                else
                    return Long.valueOf(value.toString());
            case "java.lang.Integer":
            case "int":
                if (value instanceof BigDecimal)
                    return ((BigDecimal) value).intValue();
                else
                    return Integer.valueOf(value.toString());
            case "java.lang.Short":
            case "short":
                if (value instanceof BigDecimal)
                    return ((BigDecimal) value).shortValue();
                else
                    return Short.valueOf(value.toString());
            case "java.lang.Byte":
            case "byte":
                if (value instanceof BigDecimal)
                    return ((BigDecimal) value).byteValue();
                else
                    return Byte.valueOf(value.toString());
            case "java.lang.Character":
            case "char":
                return value.toString().charAt(0);
            case "java.lang.Boolean":
            case "boolean":
                return Boolean.valueOf(value.toString());
            default:
                throw new AutoExcelException(String.format("Can't convert %s (%s) to %s", value, value.getClass().getName(),
                        targetClass.getName()));
        }
    }
}
