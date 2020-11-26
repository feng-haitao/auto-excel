package net.fenghaitao.parameters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Setting for the column displayed in the excel.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldSetting {
    /**
     * Field name of the object
     */
    private String fieldName;
    /**
     * The column name displayed in the final excel
     */
    private String displayName;
}
