package net.fenghaitao.parameters;

import net.fenghaitao.DataSourceType;

import java.util.List;

public abstract class ExportPara {
    /**
     * Data source
     */
    private Object dataSource;
    private DataSourceType dataSourceType;
    private Class objectType;
    private int recordCount;

    public Object getDataSource() {
        return dataSource;
    }

    public void setDataSource(Object value) {
        dataSource = value;
        if (dataSource != null) {
            if (dataSource instanceof List) {
                List<Object> tmpDataSource1 = (List<Object>) dataSource;
                //如果数据源是数组或列表
                if (tmpDataSource1.isEmpty()) {
                    dataSource = null;
                } else {
                    objectType = tmpDataSource1.get(0).getClass();
                    dataSourceType = dataSourceType.List;
                    recordCount = tmpDataSource1.size();
                }
            } else {
                //如果数据源是一个基础对象
                objectType = dataSource.getClass();
                dataSourceType = dataSourceType.BasicObject;
                recordCount = 1;
            }
        }
    }

    public DataSourceType getDataSourceType() {
        return dataSourceType;
    }

    public Class getObjectType() {
        return objectType;
    }

    public int getRecordCount() {
        return recordCount;
    }

}
