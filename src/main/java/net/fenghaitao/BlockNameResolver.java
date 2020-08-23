package net.fenghaitao;

import net.fenghaitao.managers.CellManager;
import net.fenghaitao.managers.FormulaCellManager;
import net.fenghaitao.managers.RowNoCellManager;
import net.fenghaitao.managers.AggregateCellManager;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.*;

public class BlockNameResolver<T> {
    private String dataSourceName;
    private String originalDataSourceName;
    private String sheetName;
    private Sheet sheet;
    private Map<String, CellManager> fieldNameCells = new HashMap<>();
    private Map<String, AggregateCellManager> fieldNameAggregateCells = new HashMap<>();
    private Map<String, String> fieldNameMap = new HashMap<>();
    private List<FormulaCellManager> formulaCellManagers = new ArrayList<>();
    private RowNoCellManager rowNoCellManager;
    private T Para;

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public Map<String, CellManager> getFieldNameCells() {
        return fieldNameCells;
    }

    public void setFieldNameCells(Map<String, CellManager> fieldNameCells) {
        this.fieldNameCells = fieldNameCells;
    }

    public Map<String, AggregateCellManager> getFieldNameAggregateCells() {
        return fieldNameAggregateCells;
    }

    public void setFieldNameAggregateCells(Map<String, AggregateCellManager> fieldNameAggregateCells) {
        this.fieldNameAggregateCells = fieldNameAggregateCells;
    }

    public List<FormulaCellManager> getFormulaCellManagers() {
        return formulaCellManagers;
    }

    public void setFormulaCellManagers(List<FormulaCellManager> formulaCellManagers) {
        this.formulaCellManagers = formulaCellManagers;
    }

    public RowNoCellManager getRowNoCellManager() {
        return rowNoCellManager;
    }

    public void setRowNoCellManager(RowNoCellManager rowNoCellManager) {
        this.rowNoCellManager = rowNoCellManager;
    }

    public T getPara() {
        return Para;
    }

    public void setPara(T para) {
        Para = para;
    }

    public Sheet getSheet() {
        return sheet;
    }

    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    public Map<String, String> getFieldNameMap() {
        return fieldNameMap;
    }

    public void setFieldNameMap(Map<String, String> fieldNameMap) {
        this.fieldNameMap = fieldNameMap;
    }

    public String getOriginalDataSourceName() {
        return originalDataSourceName;
    }

    public void setOriginalDataSourceName(String originalDataSourceName) {
        this.originalDataSourceName = originalDataSourceName;
    }
}
