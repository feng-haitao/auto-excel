package net.fenghaitao;

import net.fenghaitao.context.ImportContext;
import net.fenghaitao.context.ExportContext;
import net.fenghaitao.enums.AggregateType;
import net.fenghaitao.enums.DataDirection;
import net.fenghaitao.enums.DataSourceType;
import net.fenghaitao.enums.ExportType;
import net.fenghaitao.exception.AutoExcelException;
import net.fenghaitao.imports.DataSet;
import net.fenghaitao.imports.ExcelReader;
import net.fenghaitao.utils.CellUtil;
import net.fenghaitao.utils.WorkbookUtil;
import net.fenghaitao.export.BlockNameResolver;
import net.fenghaitao.utils.SheetUtil;
import net.fenghaitao.export.managers.*;
import net.fenghaitao.parameters.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AutoExcel {
    private static final String regCellName = "'?%s'?!\\$([a-z]+)\\$([0-9]+)";
    private static final Pattern cellRefPattern = Pattern.compile("([a-z]+)([0-9]+)", Pattern.CASE_INSENSITIVE);

    /**
     * Generate Excel according to the specified template and parameters
     *
     * @param templatePath       the excel template path
     * @param outputPath         output path
     * @param templateExportPara the parameter for exporting with template
     */
    public static void save(String templatePath, String outputPath, TemplateExportPara templateExportPara) {
        save(templatePath, outputPath, Arrays.asList(templateExportPara));
    }

    /**
     * Generate Excel according to the specified template and parameters
     *
     * @param templatePath        the excel template path
     * @param outputPath          output path
     * @param templateExportParas the parameters for exporting with template
     */
    public static void save(String templatePath, String outputPath, List<TemplateExportPara> templateExportParas) {
        save(templatePath, outputPath, templateExportParas, null, null);
    }

    /**
     * Generate Excel according to the specified template and parameters
     *
     * @param templatePath        the excel template path
     * @param outputPath          output path
     * @param templateExportParas the parameters for exporting with template
     * @param excelSetting        Excel Setting
     */
    public static void save(String templatePath, String outputPath, List<TemplateExportPara> templateExportParas,
                            ExcelSetting excelSetting) {
        Consumer<Workbook> actionBehind = null;
        if (excelSetting != null && excelSetting.getRemovedSheets() != null) {
            actionBehind = workbook -> {
                excelSetting.getRemovedSheets().forEach(sheetName -> {
                    int sheetIndex = workbook.getSheetIndex(sheetName);
                    if (sheetIndex >= 0)
                        workbook.removeSheetAt(sheetIndex);
                });
            };
        }
        save(templatePath, outputPath, templateExportParas, null, actionBehind);
    }

    /**
     * Generate Excel according to the specified template and parameters
     *
     * @param templatePath        the excel template path
     * @param outputPath          output path
     * @param templateExportParas the parameters for exporting with template
     * @param actionAhead         action before internal operations
     * @param actionBehind        action after internal operations
     */
    public static void save(String templatePath,
                            String outputPath,
                            List<TemplateExportPara> templateExportParas,
                            Consumer<Workbook> actionAhead,
                            Consumer<Workbook> actionBehind) {
        if (!(new File(templatePath)).exists())
            throw new AutoExcelException("Cannot find template file: " + templatePath);

        ExportContext exportContext = new ExportContext(templatePath, ExportType.Template);
        Workbook workbook = exportContext.getWorkbook();

        //action before internal operations
        if (actionAhead != null)
            actionAhead.accept(workbook);

        List<BlockNameResolver> blockNameResolvers = resolveCellNames(workbook, templateExportParas, exportContext);

        //cache the properties of data source to be used
        Map<String, Map<String, Field>> dataSourceNameFields = new HashMap<>(16);
        for (TemplateExportPara templateExportPara : templateExportParas) {
            if (templateExportPara.getDataSource() != null && templateExportPara.getRecordCount() > 0)
                dataSourceNameFields.put(templateExportPara.getDataSourceName().toLowerCase(), mapFieldNameField(templateExportPara.getObjectType()));
        }

        boolean forceFormulaRecalculation = false;
        //insert data into template
        for (BlockNameResolver blockNameResolver : blockNameResolvers) {
            TemplateExportPara templateExportPara = blockNameResolver.getPara();
            if (templateExportPara == null) continue;

            Object dataSource = templateExportPara.getDataSource();
            if (dataSource == null) continue;

            Sheet sheet = blockNameResolver.getSheet();
            int recordCount = templateExportPara.getRecordCount();
            try {
                //if data source is List
                if (templateExportPara.getDataSourceType() == DataSourceType.List) {
                    if (templateExportPara.isInserted() && recordCount > 1) {
                        int startRow = blockNameResolver.getFieldNameCells().entrySet().iterator()
                            .next().getValue().getRowIndex() + 1;
                        Sheet xssfSheet = exportContext.getXssfSheet(blockNameResolver.getSheetName());
                        //Move the other rows to free up enough space to populate the current data source
                        xssfSheet.shiftRows(startRow, xssfSheet.getLastRowNum(), recordCount - 1, true, false);
                        //After the row is moved, the original cell position changes and the cell location saved in the cell
                        //name manager needs to be refreshed
                        reLocate(blockNameResolvers, blockNameResolver.getSheetName(), exportContext);
                    }

                    int step = 0;
                    for (Object record : (List) dataSource) {
                        writeRecordByCellName(blockNameResolver, record, dataSourceNameFields, step,
                            exportContext);
                        writeFormula(blockNameResolver, step, exportContext);
                        writeRowNo(blockNameResolver, step, exportContext);
                        ++step;
                    }
                    writeAggregate(blockNameResolver, recordCount, exportContext);

                    //if data direction is right, the column width needs to be adaptive
                    if (templateExportPara.getDataDirection() == DataDirection.Right) {
                        int startColIndex = blockNameResolver.getFieldNameCells().entrySet().iterator()
                            .next().getValue().getColIndex();
                        SheetUtil.setColumnWidth(sheet, startColIndex, recordCount, exportContext);
                    }

                    if (blockNameResolver.getFormulaCellManagers().size() > 0 || blockNameResolver.getFieldNameAggregateCells().size() > 0)
                        forceFormulaRecalculation = true;
                }
                //if data source is a basic object
                else {
                    writeRecordByCellName(blockNameResolver, dataSource, dataSourceNameFields, 0,
                        exportContext);
                }
            }
            catch (IllegalAccessException e) {
                throw new AutoExcelException(e);
            }
        }

        //action after internal operations
        if (actionBehind != null)
            actionBehind.accept(workbook);

        if (forceFormulaRecalculation)
            workbook.setForceFormulaRecalculation(true);

        exportContext.end(outputPath);
    }

    /**
     * Generate Excel directly
     *
     * @param outputPath       output path
     * @param directExportPara the parameter for exporting directly
     */
    public static void save(String outputPath, DirectExportPara directExportPara) {
        List<DirectExportPara> directExportParas = new ArrayList<>();
        directExportParas.add(directExportPara);
        save(outputPath, directExportParas);
    }

    /**
     * Generate Excel directly
     *
     * @param outputPath        output path
     * @param directExportParas the parameters for exporting directly
     */
    public static void save(String outputPath, List<DirectExportPara> directExportParas) {
        ExportContext exportContext = new ExportContext(ExportType.Direct);
//        Workbook workbook = exportContext.getWorkbook();

        for (DirectExportPara directExportPara : directExportParas) {
            createSheet(exportContext,directExportPara);
        }
        exportContext.end(outputPath);
    }

    public static void createSheet(ExportContext exportContext,DirectExportPara directExportPara) {
        Workbook workbook = exportContext.getWorkbook();
        Sheet sheet;
        if (directExportPara.getDataSource() == null)
            return;

        if (StringUtils.isEmpty(directExportPara.getSheetName()))
            sheet = workbook.createSheet();
        else
            sheet = workbook.createSheet(directExportPara.getSheetName());

        DataSourceType dataSourceType = directExportPara.getDataSourceType();
        Map<String, Field> fieldNameFields = mapFieldNameField(directExportPara.getObjectType());

        List<FieldSetting> fieldSettings = directExportPara.getFieldSettings();
        //auto generate filedSettings,use filed name as display name
        if (fieldSettings == null || fieldSettings.size() == 0) {
            fieldSettings = fieldNameFields.values()
                    .stream()
                    .map(m -> new FieldSetting(m.getName(), m.getName()))
                    .collect(Collectors.toList());
        }
        int rowIndex = 0;
        int colIndex = 0;
        //write title
        for (FieldSetting fieldSetting : fieldSettings) {
            SheetUtil.setValue(sheet, rowIndex, colIndex, fieldSetting.getDisplayName(), exportContext)
                    .setCellStyle(exportContext.getDefaultHeadStyle());
            exportContext.refreshMaxColumnWidth(sheet.getSheetName(), colIndex, fieldSetting.getDisplayName());
            ++colIndex;
        }
        //write data
        ++rowIndex;
        try {
            if (dataSourceType == DataSourceType.List) {
                for (Object record : (List) directExportPara.getDataSource()) {
                    if (record instanceof List) {
                        writeRecordByFieldSetting(sheet, fieldSettings, record,
                                rowIndex, exportContext);
                    } else {
                        writeRecordByFieldSetting(sheet, fieldSettings, record,fieldNameFields,
                                rowIndex, exportContext);
                    }
                    ++rowIndex;
                }
            } else {
                writeRecordByFieldSetting(sheet, fieldSettings, directExportPara.getDataSource(),
                        fieldNameFields, rowIndex, exportContext);
            }
        }
        catch (IllegalAccessException e) {
            throw new AutoExcelException(e);
        }
        SheetUtil.setColumnWidth(sheet, 0, fieldSettings.size(), exportContext);

    }

    /**
     * Resolve all of the cell name in the workbook
     */
    private static List<BlockNameResolver> resolveCellNames(Workbook workbook,
                                                            List<TemplateExportPara> paras,
                                                            ExportContext exportContext) {
        //find out all cell name in the template and sort them
        List<Name> names = new ArrayList<>(workbook.getAllNames());
        names.sort(Comparator.comparing(a -> a.getNameName().toLowerCase()));

        String currDataSourceName = "";
        BlockNameResolver tmpBlockNameResolver = null;
        List<BlockNameResolver> blockNameResolvers = new ArrayList<>();
        Sheet sheet = null;
        Pattern pattern = null;
        //Iterate over all cell names, categorize by data source name, and resolve the column and row indexes corresponding to cell names
        for (Name name : names) {
            //'name.IsDeleted == true' means the cell doesn't exist
            if (name.isDeleted()) continue;

            String sheetName = name.getSheetName();
            String cellName = name.getNameName();
            /*
            * format of cell name:
            * 1. dataSourceName.fieldName[.aggregateType], eg. cb_product.SaleArea.sum
            * 2. dataSourceName.formula.xxxx, eg. cb_product.formula.1
            * 3. dataSourceName.RowNo, eg. cb_product.RowNo
            */
            String[] arr = cellName.split("\\.");
            if (arr.length < 2) continue;

            if (!currDataSourceName.equalsIgnoreCase(arr[0])) {
                currDataSourceName = arr[0].toLowerCase();

                String finalCurrDataSourceName = currDataSourceName;
                Optional<TemplateExportPara> para = paras.stream()
                        .filter(m -> m.getDataSourceName().equalsIgnoreCase(finalCurrDataSourceName))
                        .findFirst();

                sheet = workbook.getSheet(sheetName);
                tmpBlockNameResolver = new BlockNameResolver();
                tmpBlockNameResolver.setDataSourceName(currDataSourceName);
                tmpBlockNameResolver.setSheetName(sheetName);
                tmpBlockNameResolver.setSheet(sheet);
                if (para.isPresent()) {
                    tmpBlockNameResolver.setPara(para.get());
                    tmpBlockNameResolver.setOriginalDataSourceName(para.get().getDataSourceName());
                } else {
                    tmpBlockNameResolver.setOriginalDataSourceName(arr[0]);
                }

                blockNameResolvers.add(tmpBlockNameResolver);

                String regex = String.format(regCellName, Pattern.quote(sheetName));
                pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            }

            Matcher matcher = pattern.matcher(name.getRefersToFormula());
            if (!matcher.matches()) continue;

            int rowIndex = Integer.parseInt(matcher.group(2)) - 1;
            int colIndex = WorkbookUtil.colNameToIndex(matcher.group(1));
            Cell cell = SheetUtil.getOrCreateCell(sheet, rowIndex, colIndex, exportContext);
            String secondPart = arr[1].toLowerCase();
            //When the current cell is a ternary cell
            if (arr.length == 3) {
                if (secondPart.equals("formula")) {
                    if (cell.getCellType() != CellType.FORMULA)
                        continue;

                    FormulaCellManager formulaCellManager = new FormulaCellManager();
                    formulaCellManager.setCellName(cellName);
                    formulaCellManager.setRowIndex(rowIndex);
                    formulaCellManager.setColIndex(colIndex);
                    formulaCellManager.setFormula(cell.getCellFormula());
                    formulaCellManager.setCellStyle(cell.getCellStyle());
                    tmpBlockNameResolver.getFormulaCellManagers().add(formulaCellManager);
                } else {
                    AggregateType aggregateType = AggregateType.NONE;
                    try {
                        aggregateType = AggregateType.valueOf(arr[2].toUpperCase());
                    } catch (Exception ex) {
                        continue;
                    }

                    AggregateCellManager aggregateCellManager = new AggregateCellManager();
                    aggregateCellManager.setCellName(cellName);
                    aggregateCellManager.setRowIndex(rowIndex);
                    aggregateCellManager.setColIndex(colIndex);
                    aggregateCellManager.setAggregateType(aggregateType);
                    tmpBlockNameResolver.getFieldNameAggregateCells().put(secondPart, aggregateCellManager);
                }
            } else {
                if (secondPart.equals("rowno")) {
                    RowNoCellManager rowNoCellManager = new RowNoCellManager();
                    rowNoCellManager.setCellName(cellName);
                    rowNoCellManager.setRowIndex(rowIndex);
                    rowNoCellManager.setColIndex(colIndex);
                    rowNoCellManager.setCellStyle(cell.getCellStyle());
                    tmpBlockNameResolver.setRowNoCellManager(rowNoCellManager);
                } else {
                    CellManager cellManager = new CellManager();
                    cellManager.setCellName(cellName);
                    cellManager.setRowIndex(rowIndex);
                    cellManager.setColIndex(colIndex);
                    cellManager.setColName(matcher.group(1));
                    cellManager.setCellStyle(cell.getCellStyle());
                    tmpBlockNameResolver.getFieldNameCells().put(secondPart, cellManager);
                    tmpBlockNameResolver.getFieldNameMap().put(secondPart, arr[1]);
                }
            }
        }

        return blockNameResolvers;
    }

    /**
     * Generate filedName-properties key value mapping
     */
    private static Map<String, Field> mapFieldNameField(Class aClass) {
        Map<String, Field> result = new HashMap<>(16);
        for (Field field : aClass.getDeclaredFields())
            result.put(field.getName().toLowerCase(), field);

        return result;
    }

    /**
     * ReLocate the cell. After the row is moved, the original cell position changes and the cell location saved in the
     * cell name manager needs to be refreshed
     */
    private static void reLocate(List<BlockNameResolver> blockNameResolvers, String currSheetName, ExportContext exportContext) {
        for (BlockNameResolver blockNameResolver : blockNameResolvers) {
            if (!blockNameResolver.getSheetName().equals(currSheetName))
                continue;

            String regex = String.format(regCellName, currSheetName);
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

            Workbook workbook = exportContext.getWorkbook();
            reLocate(workbook, pattern, blockNameResolver.getFieldNameCells().values());
            reLocate(workbook, pattern, blockNameResolver.getFieldNameAggregateCells().values());
            reLocate(workbook, pattern, blockNameResolver.getFormulaCellManagers());
            reLocate(workbook, pattern, Arrays.asList(blockNameResolver.getRowNoCellManager()));

            blockNameResolver.getFormulaCellManagers().forEach(formulaCellMgr -> {
                Cell cell = SheetUtil.getOrCreateCell(blockNameResolver.getSheet(), formulaCellMgr.getRowIndex(),
                        formulaCellMgr.getColIndex(), exportContext);
                formulaCellMgr.setFormula(cell.getCellFormula());
            });
        }
    }

    private static void reLocate(Workbook workbook, Pattern pattern, Collection<? extends BaseCellManager> baseCellManagers) {
        for (BaseCellManager baseCellManager : baseCellManagers) {
            String refer = workbook.getName(baseCellManager.getCellName()).getRefersToFormula();
            Matcher matcher = pattern.matcher(refer);
            if (matcher.find()) {
                baseCellManager.setRowIndex(Integer.parseInt(matcher.group(2)) - 1);
                baseCellManager.setColIndex(WorkbookUtil.colNameToIndex(matcher.group(1)));
            }
        }
    }

    /**
     * Write a record to sheet according to the cell name
     */
    private static void writeRecordByCellName(BlockNameResolver blockNameResolver,
                                              Object dataSourceRecord,
                                              Map<String, Map<String, Field>> dataSourceNameFields,
                                              int step,
                                              ExportContext exportContext) throws IllegalAccessException {
        for (Map.Entry<String, CellManager> entry : blockNameResolver.getFieldNameCells().entrySet()) {
            String fieldName = entry.getKey();
            CellManager cellManager = entry.getValue();
            String dataSourceName = blockNameResolver.getDataSourceName();
            if (dataSourceNameFields.containsKey(dataSourceName) && dataSourceNameFields.get(dataSourceName).containsKey(fieldName)) {
                Field field = dataSourceNameFields.get(dataSourceName).get(fieldName);
                field.setAccessible(true);
                Object cellValue = field.get(dataSourceRecord);
                int rowIndex = cellManager.getRowIndex();
                int colIndex = cellManager.getColIndex();

                DataDirection dataDirection = blockNameResolver.getPara().getDataDirection();
                if (dataDirection == DataDirection.Down)
                    rowIndex += step;
                else
                    colIndex += step;

                Cell cell = SheetUtil.setValue(blockNameResolver.getSheet(), rowIndex, colIndex, cellValue, exportContext);
                if (dataDirection == DataDirection.Right)
                    exportContext.refreshMaxColumnWidth(blockNameResolver.getSheet().getSheetName(), colIndex, cellValue);

                if (blockNameResolver.getPara().isCopyCellStyle())
                    cell.setCellStyle(cellManager.getCellStyle());
            }
        }
    }

    /**
     * Write a record to sheet according to the fieldSettings
     */
    private static void writeRecordByFieldSetting(Sheet sheet,
                                                  List<FieldSetting> fieldSettings,
                                                  Object dataSourceRecord,
                                                  Map<String, Field> fieldNameFields,
                                                  int rowIndex,
                                                  ExportContext exportContext) throws IllegalAccessException {
        int colIndex = 0;
        for (FieldSetting fieldSetting : fieldSettings) {
            String fieldName = fieldSetting.getFieldName().toLowerCase();
            if (fieldNameFields.containsKey(fieldName)) {
                Field field = fieldNameFields.get(fieldName);
                field.setAccessible(true);
                Object value = field.get(dataSourceRecord);
                Cell cell = SheetUtil.setValue(sheet, rowIndex, colIndex, value, exportContext);
                CellUtil.setStyle(cell, value, exportContext);
                exportContext.refreshMaxColumnWidth(sheet.getSheetName(), colIndex, value);
            }
            ++colIndex;
        }
    }

    /**
     * Write a record to sheet according to the fieldSettings
     */
    private static void writeRecordByFieldSetting(Sheet sheet,
                                                  List<FieldSetting> fieldSettings,
                                                  Object dataSourceRecord,
                                                  int rowIndex,
                                                  ExportContext exportContext) throws IllegalAccessException {
        int colIndex = 0;
        List listData = (List) dataSourceRecord;
        for (int i = 0; i < fieldSettings.size(); i++) {
            Object value = listData.get(i);
            Cell cell = SheetUtil.setValue(sheet, rowIndex, colIndex, value, exportContext);
            CellUtil.setStyle(cell, value, exportContext);
            exportContext.refreshMaxColumnWidth(sheet.getSheetName(), colIndex, value);
            colIndex++;
        }
    }





    /**
     * Assign value to the aggregate cells
     */
    private static void writeAggregate(BlockNameResolver blockNameResolver, int recordCount, ExportContext exportContext) {
        for (String fieldName : blockNameResolver.getFieldNameAggregateCells().keySet()) {
            AggregateCellManager aggregateCellManager = blockNameResolver.getFieldNameAggregateCells().get(fieldName);
            Cell aggregateCell = SheetUtil.getOrCreateCell(blockNameResolver.getSheet(), aggregateCellManager.getRowIndex(),
                    aggregateCellManager.getColIndex(), exportContext);
            if (blockNameResolver.getFieldNameCells().containsKey(fieldName)) {
                CellManager cellMgr = blockNameResolver.getFieldNameCells().get(fieldName);
                int rowIndex = cellMgr.getRowIndex();
                int colIndex = cellMgr.getColIndex();
                String colName = cellMgr.getColName();
                switch (aggregateCellManager.getAggregateType()) {
                    case SUM:
                        if (blockNameResolver.getPara().getDataDirection() == DataDirection.Down)
                            aggregateCell.setCellFormula(String.format("SUM(%1$s%2$s:%1$s%3$s)", colName,
                                    rowIndex + 1, rowIndex + recordCount));
                        else
                            aggregateCell.setCellFormula(String.format("SUM(%1$s%2$s:%3$s%2$s)", colName,
                                    rowIndex + 1, WorkbookUtil.indexToColName(colIndex + recordCount - 1)));
                        break;
                    case AVG:
                        if (blockNameResolver.getPara().getDataDirection() == DataDirection.Down)
                            aggregateCell.setCellFormula(String.format("AVERAGE(%1$s%2$s:%1$s%3$s)", colName,
                                    rowIndex + 1, rowIndex + recordCount));
                        else
                            aggregateCell.setCellFormula(String.format("AVERAGE(%1$s%2$s:%3$s%2$s)", colName,
                                    rowIndex + 1, WorkbookUtil.indexToColName(colIndex + recordCount - 1)));
                        break;
                }
            }
        }
    }

    /**
     * Write formula
     */
    private static void writeFormula(BlockNameResolver blockNameResolver, int step, ExportContext exportContext) {
        if (step < 1)
            return;

        blockNameResolver.getFormulaCellManagers().forEach(formulaCellManager -> {
            Matcher matcher = cellRefPattern.matcher(formulaCellManager.getFormula());
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                String colName = matcher.group(1);
                String rowIndex = matcher.group(2);
                String replacement;
                if (blockNameResolver.getPara().getDataDirection() == DataDirection.Down)
                    replacement = colName + (Integer.parseInt(rowIndex) + step);
                else
                    replacement = WorkbookUtil.getColName(colName, step) + rowIndex;

                matcher.appendReplacement(sb, replacement);
            }
            matcher.appendTail(sb);
            String formula = sb.toString();

            int newRowIndex = formulaCellManager.getRowIndex();
            int newColIndex = formulaCellManager.getColIndex();
            if (blockNameResolver.getPara().getDataDirection() == DataDirection.Down)
                newRowIndex += step;
            else
                newColIndex += step;

            Cell cell = SheetUtil.getOrCreateCell(blockNameResolver.getSheet(), newRowIndex, newColIndex, exportContext);
            cell.setCellFormula(formula);
            cell.setCellStyle(formulaCellManager.getCellStyle());
        });
    }

    /**
     * Write row no.
     */
    private static void writeRowNo(BlockNameResolver blockNameResolver, int step, ExportContext exportContext) {
        RowNoCellManager rowNoCellMgr = blockNameResolver.getRowNoCellManager();
        if (rowNoCellMgr == null || blockNameResolver.getPara().getDataDirection() != DataDirection.Down)
            return;

        SheetUtil.setValue(blockNameResolver.getSheet(), rowNoCellMgr.getRowIndex() + step, rowNoCellMgr.getColIndex(),
                step + 1, exportContext)
            .setCellStyle(rowNoCellMgr.getCellStyle());
    }

    /**
     * Import data from excel
     *
     * @param fileName    file name to read
     * @param importParas the parameter for import
     * @return
     */
    public static DataSet read(String fileName, List<ImportPara> importParas) {
        if (!(new File(fileName)).exists())
            throw new AutoExcelException("File not found: " + fileName);

        ImportContext importContext = new ImportContext(importParas);
        ExcelReader reader = new ExcelReader(importContext);
        reader.process(fileName);
        return importContext.getDataSet();
    }
}