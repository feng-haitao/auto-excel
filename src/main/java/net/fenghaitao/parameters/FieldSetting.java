package net.fenghaitao.parameters;

/**
 * Setting for the column displayed in the final excel.
 */
public class FieldSetting {
    public FieldSetting() {
    }

    public FieldSetting(String fieldName, String displayName) {
        this.fieldName = fieldName;
        this.displayName = displayName;
    }

    /**
     * Field name of the object
     */
    private String fieldName;

    /**
     * The column name displayed in the final excel
     */
    private String displayName;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
