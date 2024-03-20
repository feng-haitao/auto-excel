package net.fenghaitao.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ClassUtil {
    /**
     * Generate filedName-properties key value mapping
     */
    public static Map<String, Field> mapFieldNameField(Class aClass) {
        Map<String, Field> result = new HashMap<>(16);
        for (Field field : aClass.getDeclaredFields())
            result.put(field.getName().toLowerCase(), field);

        return result;
    }
}
