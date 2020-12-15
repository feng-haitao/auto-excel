package net.fenghaitao.context;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import net.fenghaitao.constant.ExcelConst;
import net.fenghaitao.enums.ExportType;
import net.fenghaitao.exception.AutoExcelException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
public class ExportContext {
    private Workbook workbook;
    private Workbook xssfWorkbook;

    @Setter(AccessLevel.NONE)
    private CellStyle defaultDateStyle;

    @Setter(AccessLevel.NONE)
    private CellStyle defaultNumericStyle;

    @Setter(AccessLevel.NONE)
    private CellStyle defaultHeadStyle;

    private ExportType exportType;

    @Setter(AccessLevel.NONE)
    private boolean isExportDirectly;

    @Setter(AccessLevel.NONE)
    private Map<String, Sheet> sheetNameXssfSheets = new HashMap<>();

    /**
     * sheet name - column index - column width map
     */
    @Setter(AccessLevel.NONE)
    private Map<String, Map<Integer, Integer>> sheetColumnWidthMap = new HashMap<>();

    public ExportContext(ExportType exportType) {
        this(null, exportType);
    }

    public ExportContext(String templatePath, ExportType exportType) {
        try {
            if (templatePath == null)
                this.xssfWorkbook = new XSSFWorkbook();
            else
                this.xssfWorkbook = new XSSFWorkbook(templatePath);

            workbook = new SXSSFWorkbook((XSSFWorkbook) xssfWorkbook, 500);
            this.exportType = exportType;
            isExportDirectly = exportType.equals(ExportType.Direct);
            if (isExportDirectly)
                createDefaultStyle();

            initXssfSheets();

        } catch (IOException e) {
            throw new AutoExcelException(e);
        }
    }

    public void initXssfSheets() {
        for (int i = 0; i < xssfWorkbook.getNumberOfSheets(); ++i) {
            Sheet sheet = xssfWorkbook.getSheetAt(i);
            sheetNameXssfSheets.put(sheet.getSheetName(), sheet);
        }
    }

    public Sheet getXssfSheet(String sheetName) {
        return sheetNameXssfSheets.get(sheetName);
    }

    public void end(String outputPath) {
        try (FileOutputStream stream = new FileOutputStream(outputPath)) {
            workbook.write(stream);
        } catch (IOException e) {
            throw new AutoExcelException(e);
        }
        // dispose of temporary files backing this workbook on disk
        ((SXSSFWorkbook) workbook).dispose();
    }

    public void refreshMaxColumnWidth(String sheetName, int colIndex, Object value) {
        if (value == null)
            return;

        int needColWidth;
        Integer curColWidth = null;
        Map<Integer, Integer> columnWidthMap = sheetColumnWidthMap.get(sheetName);
        if (columnWidthMap == null) {
            columnWidthMap = new HashMap<>(16);
            sheetColumnWidthMap.put(sheetName, columnWidthMap);
        }
        else {
            curColWidth = columnWidthMap.get(colIndex);
        }

        if (value instanceof BigDecimal) {
            needColWidth = value.toString().length() + 4;
        } else if (value instanceof Number) {
            needColWidth = value.toString().length() + 4;
        } else if (value instanceof Date) {
            needColWidth = ExcelConst.DEFAULT_DATE_FORMAT.length() + 1;
        } else {
            needColWidth = value.toString().length();
        }

        if (needColWidth > ExcelConst.MAX_COLUMN_WIDTH)
            needColWidth = ExcelConst.MAX_COLUMN_WIDTH;

        if (curColWidth == null || needColWidth > curColWidth)
            columnWidthMap.put(colIndex, needColWidth);
    }

    private void createDefaultStyle() {
        DataFormat dataFormat = workbook.createDataFormat();

        //create style of title
        Font font = workbook.createFont();
        font.setColor(IndexedColors.WHITE.index);
        defaultHeadStyle = workbook.createCellStyle();
        defaultHeadStyle.setFillForegroundColor(IndexedColors.ORCHID.getIndex());
        defaultHeadStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        defaultHeadStyle.setFont(font);

        defaultDateStyle = workbook.createCellStyle();
        defaultDateStyle.setDataFormat(dataFormat.getFormat(ExcelConst.DEFAULT_DATE_FORMAT));
        defaultDateStyle.setAlignment(HorizontalAlignment.CENTER);

        defaultNumericStyle = workbook.createCellStyle();
        defaultNumericStyle.setDataFormat(dataFormat.getFormat(ExcelConst.DEFAULT_NUMERIC_FORMAT));
    }
}
