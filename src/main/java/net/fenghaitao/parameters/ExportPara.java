package net.fenghaitao.parameters;

import lombok.Getter;
import net.fenghaitao.enums.DataSourceType;

import java.util.List;

public abstract class ExportPara {
    /**
     * Data source
     */
    private Object dataSource;
    @Getter
    private DataSourceType dataSourceType;
    @Getter
    private Class objectType;
    @Getter
    private int recordCount;

    public Object getDataSource() {
        return dataSource;
    }

    public void setDataSource(Object value) {
        dataSource = value;
        if (dataSource != null) {
            if (dataSource instanceof List) {
                List<Object> tmpDataSource1 = (List<Object>) dataSource;
                // If the data source is an array or list
                if (tmpDataSource1.isEmpty()) {
                    dataSource = null;
                } else {
                    objectType = tmpDataSource1.get(0).getClass();
                    dataSourceType = DataSourceType.List;
                    recordCount = tmpDataSource1.size();
                }
            } else {
                // If the data source is a base object
                objectType = dataSource.getClass();
                dataSourceType = DataSourceType.BasicObject;
                recordCount = 1;
            }
        }
    }
}
