package net.fenghaitao.parameters;

import lombok.Data;
import net.fenghaitao.enums.DataDirection;

/**
 * The parameter for exporting with template
 */
@Data
public class TemplateExportPara extends ExportPara implements TemplatePara {
    /**
     * Name of data source
     */
    private String dataSourceName;
    /**
     * Whether to insert new rows in excel
     */
    private boolean isInserted;
    /**
     * Data filling direction, default is DataDirection.Down
     */
    private DataDirection dataDirection = DataDirection.Down;
    /**
     * Whether to copy cell style, default is true
     */
    private boolean copyCellStyle = true;

    public TemplateExportPara(String dataSourceName, Object dataSource) {
        this.dataSourceName = dataSourceName;
        super.setDataSource(dataSource);
    }
}