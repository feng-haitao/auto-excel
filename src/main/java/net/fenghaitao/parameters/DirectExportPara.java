package net.fenghaitao.parameters;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

import static net.fenghaitao.utils.ClassUtil.mapFieldNameField;

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
        this(dataSource,null);
    }

    public DirectExportPara(Object dataSource, List<FieldSetting> fieldSettings) {
        this(dataSource,null,fieldSettings);

    }

    public DirectExportPara(Object dataSource, String sheetName, List<FieldSetting> fieldSettings) {
        super.setDataSource(dataSource);
        this.sheetName = sheetName;

        //auto generate filedSettings,use filed name as display name
        if (CollectionUtils.isEmpty(fieldSettings)) {
            fieldSettings = mapFieldNameField(this.getObjectType()).values()
                    .stream()
                    .map(m -> new FieldSetting(m.getName(), m.getName()))
                    .collect(Collectors.toList());
        }
        this.fieldSettings = fieldSettings;
    }
}

