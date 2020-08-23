package net.fenghaitao.utils;

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.usermodel.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CellUtil {
    /**
     * Set the value of the corresponding type, according to different parameter types
     */
    public static void setValue(Cell cell, Object value) {
        if (value == null)
            return;

        if (value instanceof Boolean) {
            cell.setCellValue((boolean) value);
        } else if (value instanceof BigDecimal) {
            cell.setCellValue(((BigDecimal) value).doubleValue());
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
            Workbook workbook = cell.getSheet().getWorkbook();
            CellStyle dateStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            dateStyle.setDataFormat(format.getFormat("yyyy-mm-dd"));
            cell.setCellStyle(dateStyle);
        } else {
            cell.setCellValue(value.toString());
        }
    }

    /**
     * Get the value of any type of cell
     */
    public static Object getValue(Cell cell) {
        return getValue(cell, cell.getCellType());
    }

    private static Object getValue(Cell cell, CellType type) {
        switch (type) {
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case ERROR:
                return ErrorEval.getText(cell.getErrorCellValue());
            case FORMULA:
                return getValue(cell, cell.getCachedFormulaResultType());
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    return dateFormat.format(cell.getDateCellValue());
                } else {
                    return cell.getNumericCellValue();
                }
            case STRING:
                String str = cell.getStringCellValue();
                if (str != null && !str.isEmpty())
                    return str;
                else
                    return null;
            case _NONE:
            case BLANK:
            default:
                return null;
        }
    }
}
