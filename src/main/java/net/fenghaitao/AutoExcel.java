package net.fenghaitao;

import net.fenghaitao.utils.CellUtil;
import net.fenghaitao.utils.SheetUtil;
import net.fenghaitao.managers.*;
import net.fenghaitao.parameters.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AutoExcel {
    private static final String regName = "'?%s'?!\\$([a-z]+)\\$([0-9]+)";

    /**
     * Generate Excel according to the specified template and parameters
     *
     * @param templatePath       the excel template path
     * @param outputPath         output path
     * @param templateExportPara the parameter for exporting with template
     */
    public static void save(String templatePath, String outputPath, TemplateExportPara templateExportPara) throws Exception {
        save(templatePath, outputPath, Arrays.asList(templateExportPara));
    }

    /**
     * Generate Excel according to the specified template and parameters
     *
     * @param templatePath        the excel template path
     * @param outputPath          output path
     * @param templateExportParas the parameters for exporting with template
     */
    public static void save(String templatePath, String outputPath, List<TemplateExportPara> templateExportParas) throws Exception {
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
                            ExcelSetting excelSetting) throws Exception {
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
                            Consumer<Workbook> actionBehind) throws Exception {
        if (!(new File(templatePath)).exists())
            throw new Exception("Cannot find template file: " + templatePath);

        Workbook workbook = new XSSFWorkbook(templatePath);

        //action before internal operations
        if (actionAhead != null)
            actionAhead.accept(workbook);

        List<BlockNameResolver<TemplateExportPara>> blockNameResolvers = resolveCellNames(workbook, templateExportParas);

        //cache the properties of data source to be used
        Map<String, Map<String, Field>> dataSourceNameFields = new HashMap<>();
        for (TemplateExportPara templateExportPara : templateExportParas) {
            if (templateExportPara.getDataSource() != null && templateExportPara.getRecordCount() > 0)
                dataSourceNameFields.put(templateExportPara.getDataSourceName().toLowerCase(), mapFieldNameField(templateExportPara.getObjectType()));
        }

        //insert data into template
        for (BlockNameResolver<TemplateExportPara> blockNameResolver : blockNameResolvers) {
            TemplateExportPara templateExportPara = blockNameResolver.getPara();
            if (templateExportPara == null) continue;

            Object dataSource = templateExportPara.getDataSource();
            if (dataSource == null) continue;

            Sheet sheet = blockNameResolver.getSheet();
            int recordCount = templateExportPara.getRecordCount();
            //if data source is List
            if (templateExportPara.getDataSourceType() == DataSourceType.List) {
                if (templateExportPara.isInserted() && recordCount > 1) {
                    int startRow = blockNameResolver.getFieldNameCells().entrySet().iterator().next().getValue().getRowIndex() + 1;
                    //Move the other rows to free up enough space to populate the current data source
                    sheet.shiftRows(startRow, sheet.getLastRowNum(), recordCount - 1, true, false);
                    //After the row is moved, the original cell position changes and the cell location saved in the cell
                    //name manager needs to be refreshed
                    reLocate(workbook, blockNameResolvers, blockNameResolver.getSheetName());
                }

                //if data source is List
                if (templateExportPara.getDataSourceType() == DataSourceType.List) {
                    int i = 0;
                    for (Object record : (List) dataSource) {
                        writeRecordByCellName(sheet, blockNameResolver, record, dataSourceNameFields, i);
                        ++i;
                    }
                }
                writeAggregate(sheet, blockNameResolver, recordCount);
                writeFormula(sheet, blockNameResolver, recordCount);

                RowNoCellManager rowNoCellMgr = blockNameResolver.getRowNoCellManager();
                if (templateExportPara.getDataDirection() == DataDirection.Down && rowNoCellMgr != null)
                    writeRowNo(sheet, rowNoCellMgr, recordCount);

                //if data direction is right, the column width needs to be adaptive
                if (templateExportPara.getDataDirection() == DataDirection.Right) {
                    int startColIndex = blockNameResolver.getFieldNameCells().entrySet().iterator().next().getValue().getColIndex();
                    for (int i = 0; i < recordCount; ++i)
                        sheet.autoSizeColumn(startColIndex + i);
                }
                //recalculate formula
                XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
            }
            //if data source is a basic object
            else {
                writeRecordByCellName(sheet, blockNameResolver, dataSource, dataSourceNameFields, 0);
            }
        }

        //action after internal operations
        if (actionBehind != null)
            actionBehind.accept(workbook);

        try (FileOutputStream stream = new FileOutputStream(outputPath)) {
            workbook.write(stream);
        }
    }

    /**
     * Generate Excel directly
     *
     * @param outputPath       output path
     * @param directExportPara the parameter for exporting directly
     */
    public static void save(String outputPath, DirectExportPara directExportPara) throws Exception {
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
    public static void save(String outputPath, List<DirectExportPara> directExportParas) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet;
        //create style of title
        Font font = workbook.createFont();
        font.setColor(IndexedColors.WHITE.index);
        CellStyle headStyle = workbook.createCellStyle();
        headStyle.setFillForegroundColor(IndexedColors.ORCHID.getIndex());
        headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headStyle.setFont(font);

        for (DirectExportPara directExportPara : directExportParas) {
            if (directExportPara.getDataSource() == null)
                continue;

            if (directExportPara.getSheetName() == null || directExportPara.getSheetName().isEmpty())
                sheet = workbook.createSheet();
            else
                sheet = workbook.createSheet(directExportPara.getSheetName());

            DataSourceType dataSourceType = directExportPara.getDataSourceType();
            Map<String, Field> fieldNameFields = mapFieldNameField(directExportPara.getObjectType());

            List<FieldSetting> fieldSettings = directExportPara.getFieldSettings();
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
                SheetUtil.setValue(sheet, rowIndex, colIndex, fieldSetting.getDisplayName())
                        .setCellStyle(headStyle);
                ++colIndex;
            }
            //write data
            ++rowIndex;
            if (dataSourceType == DataSourceType.List) {
                for (Object record : (List) directExportPara.getDataSource()) {
                    writeRecordByFieldSetting(sheet, fieldSettings, record, fieldNameFields, rowIndex);
                    ++rowIndex;
                }
            } else {
                writeRecordByFieldSetting(sheet, fieldSettings, directExportPara.getDataSource(),
                        fieldNameFields, rowIndex);
            }
            //auto size the columns
            for (int i = 0; i < fieldSettings.size(); ++i)
                sheet.autoSizeColumn(i);
        }

        try (FileOutputStream stream = new FileOutputStream(outputPath)) {
            workbook.write(stream);
        }
    }

    /**
     * Resolve all of the cell name in the workbook
     */
    private static <T extends TemplatePara> List<BlockNameResolver<T>> resolveCellNames(Workbook workbook, List<T> paras) {
        //find out all cell name in the template and sort them
        List<Name> names = new ArrayList<>(workbook.getAllNames());
        names.sort(Comparator.comparing(a -> a.getNameName().toLowerCase()));

        String currDataSourceName = "";
        BlockNameResolver<T> tmpBlockNameResolver = null;
        List<BlockNameResolver<T>> blockNameResolvers = new ArrayList<>();
        Sheet sheet = null;
        Pattern pattern = null;
        //Iterate over all cell names, categorize by data source name, and resolve the column and row indexes corresponding to cell names
        for (Name name : names) {
            //'name.IsDeleted == true' means the cell doesn't exist
            if (name.isDeleted()) continue;

            String sheetName = name.getSheetName();
            String cellName = name.getNameName();
            /*
            format of cell name：
            1. dataSourceName.fieldName[.aggregateType], eg. cb_product.SaleArea.sum
            2. dataSourceName.formula.xxxx, eg. cb_product.formula.1
            3. dataSourceName.RowNo, eg. cb_product.RowNo
            */
            String[] arr = cellName.split("\\.");
            if (arr.length < 2) continue;

            if (!currDataSourceName.equalsIgnoreCase(arr[0])) {
                currDataSourceName = arr[0].toLowerCase();

                String finalCurrDataSourceName = currDataSourceName;
                Optional<T> para = paras.stream()
                        .filter(m -> m.getDataSourceName().equalsIgnoreCase(finalCurrDataSourceName))
                        .findFirst();

                sheet = workbook.getSheet(sheetName);
                tmpBlockNameResolver = new BlockNameResolver<>();
                tmpBlockNameResolver.setDataSourceName(currDataSourceName);
                tmpBlockNameResolver.setOriginalDataSourceName(arr[0]);
                tmpBlockNameResolver.setSheetName(sheetName);
                tmpBlockNameResolver.setSheet(sheet);
                if (para.isPresent())
                    tmpBlockNameResolver.setPara(para.get());

                blockNameResolvers.add(tmpBlockNameResolver);

                String regex = String.format(regName, Pattern.quote(sheetName));
                pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            }

            Matcher matcher = pattern.matcher(name.getRefersToFormula());
            if (!matcher.matches()) continue;

            int rowIndex = Integer.parseInt(matcher.group(2)) - 1;
            int colIndex = colNameToIndex(matcher.group(1));
            Cell cell = SheetUtil.getOrCreateCell(sheet, rowIndex, colIndex);
            String secondPart = arr[1].toLowerCase();
            //When the current cell is a ternary cell
            if (arr.length == 3) {
                if (secondPart.equals("formula")) {
                    if (cell.getCellType() == CellType.FORMULA) {
                        FormulaCellManager formulaCellManager = new FormulaCellManager();
                        formulaCellManager.setCellName(cellName);
                        formulaCellManager.setRowIndex(rowIndex);
                        formulaCellManager.setColIndex(colIndex);
                        formulaCellManager.setFormula(cell.getCellFormula());
                        formulaCellManager.setCellStyle(cell.getCellStyle());
                        tmpBlockNameResolver.getFormulaCellManagers().add(formulaCellManager);
                    }
                } else {
                    AggregateType aggregateType = AggregateType.NONE;
                    try {
                        aggregateType = AggregateType.valueOf(arr[2].toUpperCase());
                    } catch (Exception ex) {
                        //do nothing
                    }
                    if (aggregateType != AggregateType.NONE) {
                        AggregateCellManager aggregateCellManager = new AggregateCellManager();
                        aggregateCellManager.setCellName(cellName);
                        aggregateCellManager.setRowIndex(rowIndex);
                        aggregateCellManager.setColIndex(colIndex);
                        aggregateCellManager.setAggregateType(aggregateType);
                        tmpBlockNameResolver.getFieldNameAggregateCells().put(secondPart, aggregateCellManager);
                    }
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
        Map<String, Field> result = new HashMap<>();
        for (Field field : aClass.getDeclaredFields())
            result.put(field.getName().toLowerCase(), field);

        return result;
    }

    /**
     * ReLocate the cell. After the row is moved, the original cell position changes and the cell location saved in the
     * cell name manager needs to be refreshed
     */
    private static void reLocate(Workbook workbook, List<BlockNameResolver<TemplateExportPara>> blockNameResolvers, String currSheetName) {
        for (BlockNameResolver<TemplateExportPara> blockNameResolver : blockNameResolvers) {
            if (!blockNameResolver.getSheetName().equals(currSheetName))
                continue;

            String regex = String.format(regName, currSheetName);
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

            reLocate(workbook, pattern, blockNameResolver.getFieldNameCells().values());
            reLocate(workbook, pattern, blockNameResolver.getFieldNameAggregateCells().values());
            reLocate(workbook, pattern, blockNameResolver.getFormulaCellManagers());
            reLocate(workbook, pattern, Arrays.asList(blockNameResolver.getRowNoCellManager()));

            blockNameResolver.getFormulaCellManagers().forEach(formulaCellMgr -> {
                Cell cell = SheetUtil.getOrCreateCell(blockNameResolver.getSheet(), formulaCellMgr.getRowIndex(),
                        formulaCellMgr.getColIndex());
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
                baseCellManager.setColIndex(colNameToIndex(matcher.group(1)));
            }
        }
    }

    /**
     * Write a record to sheet according to the cell name
     */
    private static void writeRecordByCellName(Sheet sheet,
                                              BlockNameResolver<TemplateExportPara> templateExcelManager,
                                              Object dataSourceRecord,
                                              Map<String, Map<String, Field>> dataSourceNameFields,
                                              int step) throws IllegalAccessException {
        for (Map.Entry<String, CellManager> entry : templateExcelManager.getFieldNameCells().entrySet()) {
            String fieldName = entry.getKey();
            CellManager cellManager = entry.getValue();
            String dataSourceName = templateExcelManager.getDataSourceName();
            if (dataSourceNameFields.containsKey(dataSourceName) && dataSourceNameFields.get(dataSourceName).containsKey(fieldName)) {
                Field field = dataSourceNameFields.get(dataSourceName).get(fieldName);
                field.setAccessible(true);
                Object cellValue = field.get(dataSourceRecord);
                int rowIndex = cellManager.getRowIndex();
                int colIndex = cellManager.getColIndex();

                if (templateExcelManager.getPara().getDataDirection() == DataDirection.Down)
                    rowIndex += step;
                else
                    colIndex += step;

                Cell cell = SheetUtil.setValue(sheet, rowIndex, colIndex, cellValue);
                if (templateExcelManager.getPara().isCopyCellStyle())
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
                                                  Map<String, Field> fieldNameFields, int rowIndex) throws IllegalAccessException {
        int colIndex = 0;
        for (FieldSetting fieldSetting : fieldSettings) {
            String fieldName = fieldSetting.getFieldName().toLowerCase();
            if (fieldNameFields.containsKey(fieldName)) {
                Field field = fieldNameFields.get(fieldName);
                field.setAccessible(true);
                Object value = field.get(dataSourceRecord);
                SheetUtil.setValue(sheet, rowIndex, colIndex, value);
            }
            ++colIndex;
        }
    }

    /**
     * Assign value to the aggregate cells
     */
    private static void writeAggregate(Sheet sheet, BlockNameResolver<TemplateExportPara> templateExcelManager, int recordCount) {
        for (String fieldName : templateExcelManager.getFieldNameAggregateCells().keySet()) {
            AggregateCellManager aggregateCellManager = templateExcelManager.getFieldNameAggregateCells().get(fieldName);
            Cell aggregateCell = SheetUtil.getOrCreateCell(sheet, aggregateCellManager.getRowIndex(), aggregateCellManager.getColIndex());
            if (templateExcelManager.getFieldNameCells().containsKey(fieldName)) {
                CellManager cellMgr = templateExcelManager.getFieldNameCells().get(fieldName);
                int rowIndex = cellMgr.getRowIndex();
                int colIndex = cellMgr.getColIndex();
                String colName = cellMgr.getColName();
                switch (aggregateCellManager.getAggregateType()) {
                    case SUM:
                        if (templateExcelManager.getPara().getDataDirection() == DataDirection.Down)
                            aggregateCell.setCellFormula(String.format("SUM(%1$s%2$s:%1$s%3$s)", colName,
                                    rowIndex + 1, rowIndex + recordCount));
                        else
                            aggregateCell.setCellFormula(String.format("SUM(%1$s%2$s:%3$s%2$s)", colName,
                                    rowIndex + 1, indexToColName(colIndex + recordCount - 1)));
                        break;
                    case AVG:
                        if (templateExcelManager.getPara().getDataDirection() == DataDirection.Down)
                            aggregateCell.setCellFormula(String.format("AVERAGE(%1$s%2$s:%1$s%3$s)", colName,
                                    rowIndex + 1, rowIndex + recordCount));
                        else
                            aggregateCell.setCellFormula(String.format("AVERAGE(%1$s%2$s:%3$s%2$s)", colName,
                                    rowIndex + 1, indexToColName(colIndex + recordCount - 1)));
                        break;
                }
            }
        }
    }

    /**
     * Write formula
     */
    private static void writeFormula(Sheet sheet, BlockNameResolver<TemplateExportPara> templateExcelManager, int recordCount) {
        templateExcelManager.getFormulaCellManagers().forEach(formulaCellManager -> {
            for (int i = 1; i < recordCount; ++i) {
                Pattern pattern = Pattern.compile("([a-z]+)([0-9]+)", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(formulaCellManager.getFormula());
                StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    String colName = matcher.group(1);
                    String rowIndex = matcher.group(2);
                    String replacement;
                    if (templateExcelManager.getPara().getDataDirection() == DataDirection.Down)
                        replacement = colName + (Integer.parseInt(rowIndex) + i);
                    else
                        replacement = getColName(colName, i) + rowIndex;

                    matcher.appendReplacement(sb, replacement);
                }
                matcher.appendTail(sb);
                String formula = sb.toString();

                int newRowIndex = formulaCellManager.getRowIndex();
                int newColIndex = formulaCellManager.getColIndex();
                if (templateExcelManager.getPara().getDataDirection() == DataDirection.Down)
                    newRowIndex += i;
                else
                    newColIndex += i;

                Cell cell = SheetUtil.getOrCreateCell(sheet, newRowIndex, newColIndex);
                cell.setCellFormula(formula);
                cell.setCellStyle(formulaCellManager.getCellStyle());
            }
        });
    }

    /**
     * Write row no.
     */
    private static void writeRowNo(Sheet sheet, RowNoCellManager rowNoCellMgr, int recordCount) {
        for (int i = 0; i < recordCount; ++i) {
            SheetUtil.setValue(sheet, rowNoCellMgr.getRowIndex() + i, rowNoCellMgr.getColIndex(), i + 1)
                    .setCellStyle(rowNoCellMgr.getCellStyle());
        }
    }

    /**
     * Get the Excel column name based on the given column name and step size
     */
    private static String getColName(String colName, int step) throws IllegalArgumentException {
        return indexToColName(colNameToIndex(colName) + step);
    }

    /**
     * Converts the column name to the index location，eg.AB -> 27
     */
    private static int colNameToIndex(String chars) {
        if (chars.isEmpty())
            return -1;

        int index = 0;
        int place = chars.length();
        int tmp = 0;

        for (int i = 0; i < chars.length(); ++i) {
            tmp = charToNum(chars.charAt(i));
            if (place > 1)
                index += 26 * (place - 1) * (tmp + 1);
            else
                index += tmp;

            --place;
        }
        return index;
    }

    /**
     * Convert letters into numerical Numbers，starting from 0
     */
    private static int charToNum(char chr) {
        return chr - 'A';
    }

    /**
     * Converts the column index (starting at 0) to the Excel column name
     */
    private static String indexToColName(int colIndex) throws IllegalArgumentException {
        List<Character> chars = new ArrayList<>();
        if (colIndex > 25) {
            int left = colIndex % 26;
            chars.add(numToChar(left));
            colIndex = colIndex / 26;
        } else {
            return String.valueOf(numToChar(colIndex));
        }
        while (colIndex > 26) {
            int left = colIndex % 26;
            chars.add(numToChar(left - 1));
            colIndex = colIndex / 26;
        }
        chars.add(numToChar(colIndex - 1));
        Collections.reverse(chars);
        return Arrays.toString(chars.toArray());
    }

    /**
     * Convert Numbers between 0 and 25 into letters
     */
    private static char numToChar(int num) throws IllegalArgumentException {
        if (num > 25 || num < 0)
            throw new IllegalArgumentException("The parameter num is out of range");

        return (char) ('A' + num);
    }

    /**
     * Gets the next column name for the specified column name
     */
    private static String nextColName(String colName) {
        boolean isFull = false;
        char[] chars = colName.toCharArray();
        for (int i = chars.length - 1; i >= 0; --i) {
            isFull = false;
            chars[i] = nextColName(chars[i]);
            if (chars[i] == 'A')
                isFull = true;
            else
                break;
        }
        if (isFull)
            return "A" + new String(chars);
        else
            return new String(chars);
    }

    /**
     * Gets the next column name for the specified column name
     */
    private static char nextColName(char chr) {
        return chr == 'Z' ? 'A' : (char) (chr + 1);
    }

    /**
     * Import data from excel
     * @param fileName file name to read
     * @param importParas the parameter for import
     */
    public static HashMap<String, List<HashMap<String, Object>>> read(String fileName, List<ImportPara> importParas) throws Exception {
        if (!(new File(fileName)).exists())
            throw new Exception("File not found: " + fileName);

        try (FileInputStream fs = new FileInputStream(fileName)) {
            return read(fs, importParas);
        }
    }

    /**
     * Import data from excel
     */
    public static HashMap<String, List<HashMap<String, Object>>> read(FileInputStream fileInputStream, List<ImportPara> importParas) throws Exception {
        Workbook workbook = new XSSFWorkbook(fileInputStream);
        return read(workbook, importParas);
    }

    /**
     * Import data from excel
     */
    private static HashMap<String, List<HashMap<String, Object>>> read(Workbook workbook, List<ImportPara> importParas) throws Exception {
        HashMap<String, List<HashMap<String, Object>>> dataSet = new HashMap<>();
        List<BlockNameResolver<ImportPara>> blockNameResolvers = resolveCellNames(workbook, importParas);
        for (BlockNameResolver<ImportPara> blockNameResolver : blockNameResolvers) {
            if (blockNameResolver.getPara() == null) continue;

            List<HashMap<String, Object>> dataTable = new ArrayList<>();
            Sheet sheet = workbook.getSheet(blockNameResolver.getSheetName());
            Map<String, CellManager> fieldNameCells = blockNameResolver.getFieldNameCells();
            int rowNum = fieldNameCells.entrySet().iterator().next().getValue().getRowIndex();

            //fill dataTable
            switch (blockNameResolver.getPara().getDataDirection()) {
                case Down:
                    for (int i = rowNum; i <= sheet.getLastRowNum(); ++i) {
                        Row row = sheet.getRow(i);
                        if (row == null) continue;      //default null if the row doesn't contain data

                        HashMap<String, Object> dataRow = new HashMap<>();
                        for (Map.Entry<String, CellManager> entrySet : fieldNameCells.entrySet()) {
                            String originalFieldName = blockNameResolver.getFieldNameMap().get(entrySet.getKey());
                            CellManager cellManager = entrySet.getValue();
                            Cell cell = row.getCell(cellManager.getColIndex());
                            if (cell != null)           //default null if the cell doesn't contain data
                                dataRow.put(originalFieldName, CellUtil.getValue(cell));
                        }
                        dataTable.add(dataRow);
                    }
                    break;

                case Right:
                    throw new Exception("This function is not implemented");

                default:
                    HashMap<String, Object> dataRow2 = new HashMap<>();
                    for (Map.Entry<String, CellManager> entrySet : fieldNameCells.entrySet()) {
                        String originalFieldName = blockNameResolver.getFieldNameMap().get(entrySet.getKey());
                        CellManager cellManager = entrySet.getValue();

                        Row row = sheet.getRow(cellManager.getRowIndex());
                        if (row == null) continue;      //default null if the row doesn't contain data

                        Cell cell = row.getCell(cellManager.getColIndex());
                        if (cell != null)           //default null if the cell doesn't contain data
                            dataRow2.put(originalFieldName, CellUtil.getValue(cell));
                    }
                    dataTable.add(dataRow2);
                    break;
            }
            dataSet.put(blockNameResolver.getOriginalDataSourceName(), dataTable);
        }
        return dataSet;
    }
}