package net.fenghaitao.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class SheetUtil {
    /**
     * Assign value to cell
     * @param sheet
     * @param rowIndex row index, starting from 0
     * @param colIndex column index, starting from 0
     * @param value the value to be assigned
     * @return
     */
    public static Cell setValue(Sheet sheet, int rowIndex, int colIndex, Object value) {
        Cell cell = getOrCreateCell(sheet, rowIndex, colIndex);
        CellUtil.setValue(cell, value);
        return cell;
    }

    /**
     * Get or create a cell
     * @param sheet
     * @param rowIndex row index, starting from 0
     * @param colIndex column index, starting from 0
     * @return
     */
    public static Cell getOrCreateCell(Sheet sheet, int rowIndex, int colIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            cell = row.createCell(colIndex);
        }
        return cell;
    }
}
