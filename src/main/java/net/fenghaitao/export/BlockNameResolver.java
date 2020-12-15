package net.fenghaitao.export;

import lombok.Data;
import net.fenghaitao.parameters.TemplateExportPara;
import net.fenghaitao.export.managers.CellManager;
import net.fenghaitao.export.managers.FormulaCellManager;
import net.fenghaitao.export.managers.RowNoCellManager;
import net.fenghaitao.export.managers.AggregateCellManager;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.*;

@Data
public class BlockNameResolver {
    private String dataSourceName;
    private String originalDataSourceName;
    private String sheetName;
    private Sheet sheet;
    /**
     * field name - CellManager map
     */
    private Map<String, CellManager> fieldNameCells = new HashMap<>();
    private Map<String, AggregateCellManager> fieldNameAggregateCells = new HashMap<>();
    /**
     * lowerCase field name - original field name map
     */
    private Map<String, String> fieldNameMap = new HashMap<>();
    private List<FormulaCellManager> formulaCellManagers = new ArrayList<>();
    private RowNoCellManager rowNoCellManager;
    private TemplateExportPara para;
}
