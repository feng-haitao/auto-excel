package net.fenghaitao.managers;

import org.apache.poi.ss.usermodel.CellStyle;

public class RowNoCellManager extends BaseCellManager {
    private CellStyle cellStyle;

    public CellStyle getCellStyle() {
        return cellStyle;
    }

    public void setCellStyle(CellStyle cellStyle) {
        this.cellStyle = cellStyle;
    }
}
