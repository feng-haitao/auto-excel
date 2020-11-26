package net.fenghaitao.utils;

import net.fenghaitao.context.ExportContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;

import java.math.BigDecimal;
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
        } else {
            cell.setCellValue(value.toString());
        }
    }

    public static void setStyle(Cell cell, Object value, ExportContext exportContext) {
        if (value == null)
            return;

        if (value instanceof BigDecimal) {
            cell.setCellStyle(exportContext.getDefaultNumericStyle());
        } else if (value instanceof Number) {
            cell.setCellStyle(exportContext.getDefaultNumericStyle());
        } else if (value instanceof Date) {
            cell.setCellStyle(exportContext.getDefaultDateStyle());
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
                    return cell.getDateCellValue();
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
