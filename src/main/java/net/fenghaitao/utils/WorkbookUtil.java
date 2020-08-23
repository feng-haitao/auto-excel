package net.fenghaitao.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;

public class WorkbookUtil {
    public static Cell getCell(Workbook workbook, String sheetName, int rowIndex, int colIndex)
    {
        return workbook.getSheet(sheetName).getRow(rowIndex).getCell(colIndex);
    }
}
