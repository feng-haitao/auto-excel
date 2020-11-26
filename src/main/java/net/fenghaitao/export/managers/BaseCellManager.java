package net.fenghaitao.export.managers;

import lombok.Data;

@Data
public abstract class BaseCellManager {
    private String cellName;
    private int rowIndex;
    private int colIndex;
}
