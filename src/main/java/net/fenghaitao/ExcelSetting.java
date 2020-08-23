package net.fenghaitao;

import java.util.List;

public class ExcelSetting {
    /**
     * The name of the worksheet to be removed
     */
    private List<String> removedSheets;

    public List<String> getRemovedSheets() {
        return removedSheets;
    }

    public void setRemovedSheets(List<String> removedSheets) {
        this.removedSheets = removedSheets;
    }
}
