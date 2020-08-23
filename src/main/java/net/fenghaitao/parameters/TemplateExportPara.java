package net.fenghaitao.parameters;

import net.fenghaitao.DataDirection;

/**
 * The parameter for exporting with template
 */
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

    @Override
    public String getDataSourceName() {
        return dataSourceName;
    }

    @Override
    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public boolean isInserted() {
        return isInserted;
    }

    public void setInserted(boolean inserted) {
        isInserted = inserted;
    }

    public DataDirection getDataDirection() {
        return dataDirection;
    }

    public void setDataDirection(DataDirection dataDirection) {
        this.dataDirection = dataDirection;
    }

    public boolean isCopyCellStyle() {
        return copyCellStyle;
    }

    public void setCopyCellStyle(boolean copyCellStyle) {
        this.copyCellStyle = copyCellStyle;
    }
}