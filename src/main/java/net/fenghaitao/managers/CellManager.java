package net.fenghaitao.managers;

import org.apache.poi.ss.usermodel.CellStyle;

public class CellManager extends BaseCellManager {
    private String colName;
    private CellStyle cellStyle;

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public CellStyle getCellStyle() {
        return cellStyle;
    }

    public void setCellStyle(CellStyle cellStyle) {
        this.cellStyle = cellStyle;
    }
}