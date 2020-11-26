package net.fenghaitao.export.managers;

import lombok.Data;
import org.apache.poi.ss.usermodel.CellStyle;

@Data
public class FormulaCellManager extends BaseCellManager {
    private String formula;
    private CellStyle cellStyle;
}