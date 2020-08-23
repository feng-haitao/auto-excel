package net.fenghaitao.parameters;

import net.fenghaitao.DataDirection;

public class ImportPara implements TemplatePara {
    private String dataSourceName;
    private DataDirection dataDirection;

    public ImportPara(String dataSourceName) {
        this.dataSourceName = dataSourceName;
        this.dataDirection = DataDirection.None;
    }

    public ImportPara(String dataSourceName, DataDirection dataDirection) {
        this.dataSourceName = dataSourceName;
        this.dataDirection = dataDirection;
    }

    @Override
    public String getDataSourceName() {
        return dataSourceName;
    }

    @Override
    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public DataDirection getDataDirection() {
        return dataDirection;
    }

    public void setDataDirection(DataDirection dataDirection) {
        this.dataDirection = dataDirection;
    }
}
