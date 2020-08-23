package net.fenghaitao.managers;

import org.apache.poi.ss.usermodel.CellStyle;

public class FormulaCellManager extends BaseCellManager {
    private String formula;
    private CellStyle cellStyle;

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public CellStyle getCellStyle() {
        return cellStyle;
    }

    public void setCellStyle(CellStyle cellStyle) {
        this.cellStyle = cellStyle;
    }
}