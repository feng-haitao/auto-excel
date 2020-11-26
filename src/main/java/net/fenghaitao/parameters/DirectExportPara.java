package net.fenghaitao.parameters;

import lombok.Data;

import java.util.List;

/**
 * The parameter for exporting directly
 */
@Data
public class DirectExportPara extends ExportPara {
    private String sheetName;
    /**
     * A list of settings for the column displayed in the final excel.
     */
    private List<FieldSetting> fieldSettings;

    public DirectExportPara(Object dataSource) {
        super.setDataSource(dataSource);
    }

    public DirectExportPara(Object dataSource, List<FieldSetting> fieldSettings) {
        super.setDataSource(dataSource);
        this.fieldSettings = fieldSettings;
    }

    public DirectExportPara(Object dataSource, String sheetName, List<FieldSetting> fieldSettings) {
        super.setDataSource(dataSource);
        this.sheetName = sheetName;
        this.fieldSettings = fieldSettings;
    }
}

