package net.fenghaitao.parameters;

import java.util.List;

/**
 * The parameter for exporting directly
 */
public class DirectExportPara extends ExportPara {
    private String sheetName;
    /**
     * A list of settings for the column displayed in the final excel.
     */
    private List<FieldSetting> fieldSettings;

    public DirectExportPara(Object dataSource) {
        super.setDataSource(dataSource);
    }

    public DirectExportPara(Object dataSource, String sheetName, List<FieldSetting> fieldSettings) {
        super.setDataSource(dataSource);
        this.sheetName = sheetName;
        this.fieldSettings = fieldSettings;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public List<FieldSetting> getFieldSettings() {
        return fieldSettings;
    }

    public void setFieldSettings(List<FieldSetting> fieldSettings) {
        this.fieldSettings = fieldSettings;
    }
}

