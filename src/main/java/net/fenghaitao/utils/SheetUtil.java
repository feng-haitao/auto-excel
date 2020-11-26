package net.fenghaitao.utils;

import net.fenghaitao.context.ExportContext;
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
     * @param exportContext
     * @return
     */
    public static Cell setValue(Sheet sheet, int rowIndex, int colIndex, Object value, ExportContext exportContext) {
        Cell cell = getOrCreateCell(sheet, rowIndex, colIndex, exportContext);
        CellUtil.setValue(cell, value);
        return cell;
    }

    /**
     * Get or create a cell
     * @param sheet
     * @param rowIndex row index, starting from 0
     * @param colIndex column index, starting from 0
     * @param exportContext
     * @return
     */
    public static Cell getOrCreateCell(Sheet sheet, int rowIndex, int colIndex, ExportContext exportContext) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            Sheet xssfSheet = exportContext.getXssfSheet(sheet.getSheetName());
            if (xssfSheet != null)
                row = xssfSheet.getRow(rowIndex);

            if (row == null) {
                try {
                    row = sheet.createRow(rowIndex);
                }
                catch (IllegalArgumentException e) {
                    if (xssfSheet != null)
                        row = xssfSheet.createRow(rowIndex);
                }
            }
        }
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            cell = row.createCell(colIndex);
        }
        return cell;
    }

    public static void setColumnWidth(Sheet sheet, int colStartIndex, int columnNum, ExportContext exportContext) {
        int colEndIndex = colStartIndex + columnNum;
        for (int i = colStartIndex; i < colEndIndex; ++i) {
            Integer columnWidth = exportContext.getSheetColumnWidthMap().get(sheet.getSheetName()).get(i);
            if (columnWidth != null)
                sheet.setColumnWidth(i, ((int) (columnWidth * 1.14388)) * 256);  // where 1.14388 is a max character width of the "Serif" font and 256 font units.
        }
    }
}
