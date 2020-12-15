package net.fenghaitao.imports;

import net.fenghaitao.context.ImportContext;
import net.fenghaitao.exception.AutoExcelException;
import net.fenghaitao.utils.ConvertUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSet {
    private Map<String, List<Map<String, Object>>> rawData;
    private ImportContext importContext;

    public DataSet(ImportContext importContext) {
        this.importContext = importContext;
        rawData = new HashMap<>();
    }

    public DataSet(Map<String, List<Map<String, Object>>> rawData) {
        this.rawData = rawData;
    }

    public List<Map<String, Object>> get(String key) {
        return rawData.get(key);
    }

    public void put(String key, List<Map<String, Object>> value) {
        rawData.put(key, value);
    }

    public <T> List<T> get(int sheetIndex, Class<T> entityClass) {
        if (importContext.getSheetIndexNames().containsKey(sheetIndex))
            return get(importContext.getSheetIndexNames().get(sheetIndex), entityClass);
        else
            throw new AutoExcelException("The specified sheet index does not exist: " + sheetIndex);
    }

    public <T> List<T> get(String sheetName, Class<T> entityClass) {
        if (!rawData.containsKey(sheetName))
            throw new AutoExcelException("The data source name or sheet name does not exist: " + sheetName);

        List<T> results = new ArrayList<>();
        Map<String, Field> fieldNameFields = mapFieldNameField(entityClass);
        try {
            for (Map<String, Object> row : rawData.get(sheetName)) {
                T instance = entityClass.newInstance();
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    String fieldName = entry.getKey().toLowerCase();
                    if (fieldNameFields.containsKey(fieldName)) {
                        Field field = fieldNameFields.get(fieldName);
                        field.setAccessible(true);
                        field.set(instance, ConvertUtil.convert(field.getType(), entry.getValue()));
                    }
                }
                results.add(instance);
            }
        } catch (Exception e) {
            throw new AutoExcelException(e);
        }

        return results;
    }

    /**
     * Generate filedName-properties key value mapping
     */
    private Map<String, Field> mapFieldNameField(Class aClass) {
        Map<String, Field> result = new HashMap<>(16);
        for (Field field : aClass.getDeclaredFields())
            result.put(field.getName().toLowerCase(), field);

        return result;
    }
}
