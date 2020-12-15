package net.fenghaitao.imports;

import net.fenghaitao.context.ImportContext;
import net.fenghaitao.exception.AutoExcelException;
import net.fenghaitao.parameters.ImportPara;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class SheetHandler extends DefaultHandler {
    /**
     * The last index or content
     */
    private StringBuilder lastIndexOrContent;

    private int totalRows = 0;
    private CellDataType cellDataType;
    private int curRow;
    private int curCol = 0;
    private String curTag;
    private ImportContext importContext;
    private Map<String, Object> dataRow;
    /**
     * style index - cell data type map
     */
    private Map<String, CellDataType> styleCellDataTypes = new HashMap<>();

    /**
     * Column index - field name map
     */
    private Map<Integer, String> columnFieldNames;

    public SheetHandler(ImportContext importContext) {
        this.importContext = importContext;
    }

    /**
     * Receive notification of the start of an element.
     *
     * @param uri The Namespace URI, or the empty string if the
     *        element has no Namespace URI or if Namespace
     *        processing is not being performed.
     * @param localName The local name (without prefix), or the
     *        empty string if Namespace processing is not being
     *        performed.
     * @param qName The qualified name (with prefix), or the
     *        empty string if qualified names are not available.
     * @param attributes The attributes attached to the element.  If
     *        there are no attributes, it shall be an empty
     *        Attributes object.
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        curTag = qName;
        switch (qName) {
            case XmlConstants.TAG_DIMENSION:
                curRow = 0;
                columnFieldNames = new HashMap<>(16);
                break;

            case XmlConstants.TAG_ROW:
                curRow = Integer.parseInt(attributes.getValue(XmlConstants.ATTRIBUTE_R)) - 1;
                curCol = 0;
                initDataRow();
                break;

            case XmlConstants.TAG_CELL:
                curCol++;
                setCellDataType(attributes);
                break;
        }
    }

    /**
     * Receive notification of character data inside an element.
     *
     * Get the index or content corresponding to the cell.
     * If the cell type is string, INLINESTR, number, date, lastContent is index.
     * If the cell type is boolean, error, formula, lastContent is the content.
     *
     * @param ch The characters.
     * @param start The start position in the character array.
     * @param length The number of characters to use from the
     *               character array.
     */
    @Override
    public void characters(char[] ch, int start, int length) {
        if (curTag.equals(XmlConstants.TAG_VALUE) || curTag.equals(XmlConstants.TAG_INLINE_STRING_VALUE)) {
            lastIndexOrContent = new StringBuilder();
            lastIndexOrContent.append(ch, start, length);
        }
    }

    /**
     * Receive notification of the end of an element.
     *
     * @param uri The Namespace URI, or the empty string if the
     *        element has no Namespace URI or if Namespace
     *        processing is not being performed.
     * @param localName The local name (without prefix), or the
     *        empty string if Namespace processing is not being
     *        performed.
     * @param qName The qualified name (with prefix), or the
     *        empty string if qualified names are not available.
     */
    @Override
    public void endElement(String uri, String localName, String qName) {
        ImportPara curImportPara = importContext.getCurImportPara();
        int titleIndex = curImportPara.getTitleIndex();
        int dataStartIndex = curImportPara.getDataStartIndex();

        switch (qName) {
            case XmlConstants.TAG_ROW:
                //If the label name is 'row', it means the end of the line has been reached
                if (curRow >= dataStartIndex) {
                    importContext.getDataSet().get(importContext.getCurSheetName()).add(dataRow);
                    totalRows++;
                }
                break;

            case XmlConstants.TAG_VALUE:
            case XmlConstants.TAG_INLINE_STRING_VALUE:
                // v => value of the cell, if the cell is a string, the value of the v tag is the index of the string
                // in the shared strings table
                Object value = getCellValue(lastIndexOrContent.toString());
                if (curRow == titleIndex) {
                    Map<String, String> fieldNameMap = importContext.getSheetFieldNames().get(importContext.getCurSheetIndex());
                    if (value != null) {
                        String strValue = value.toString();
                        String fieldName = fieldNameMap.getOrDefault(strValue, strValue);
                        columnFieldNames.put(curCol, fieldName);
                    }
                } else if (curRow >= dataStartIndex) {
                    if (columnFieldNames.containsKey(curCol))
                        dataRow.put(columnFieldNames.get(curCol), value);
                }
                break;
        }
    }

    public void setCellDataType(Attributes attributes) {
        String cellType = attributes.getValue(XmlConstants.ATTRIBUTE_T);
        // If cellType is empty, it means that the cell type is number
        if (cellType == null)
            cellType = "n";

        switch (cellType) {
            case "n":
                cellDataType = CellDataType.NUMBER;

                String strStyleIndex = attributes.getValue(XmlConstants.ATTRIBUTE_S);
                if (strStyleIndex == null)
                    break;

                CellDataType cachedCellDataType =  styleCellDataTypes.get(strStyleIndex);
                if (cachedCellDataType != null) {
                    cellDataType = cachedCellDataType;
                    break;
                }

                int styleIndex = Integer.parseInt(strStyleIndex);
                CellStyle style = importContext.getStylesTable().getStyleAt(styleIndex);
                if (DateUtil.isADateFormat(style.getDataFormat(), style.getDataFormatString()))
                    cellDataType = CellDataType.DATE;

                styleCellDataTypes.put(strStyleIndex, cellDataType);
                break;
            case "b":
                cellDataType = CellDataType.BOOL;
                break;
            case "e":
                cellDataType = CellDataType.ERROR;
                break;
            case "inlineStr":
                cellDataType = CellDataType.INLINESTR;
                break;
            case "s":
                cellDataType = CellDataType.SSTINDEX;
                break;
            case "str":
                cellDataType = CellDataType.FORMULA;
                break;
            default:
                throw new AutoExcelException("Unknown cell type: " + cellType);
        }
    }

    /**
     * Type processing on the parsed data
     * @param value   cell value
     */
    public Object getCellValue(String value) {
        switch (cellDataType) {
            case BOOL:
                char first = value.charAt(0);
                return first == '0' ? Boolean.FALSE : Boolean.TRUE;
            case ERROR:
            case FORMULA:
                return value;
            case INLINESTR:
                XSSFRichTextString rtsi = new XSSFRichTextString(value);
                return rtsi.toString();
            case SSTINDEX:
                int idx = Integer.parseInt(value);
                // Get content value based on index value
                RichTextString rts = importContext.getSharedStringsTable().getItemAt(idx);
                return rts.toString();
            case NUMBER:
                return new BigDecimal(value);
            case DATE:
                return DateUtil.getJavaDate(Double.parseDouble(value));
            default:
                return value;
        }
    }

    public void initDataRow() {
        dataRow = new HashMap<>(columnFieldNames.size() * 4 / 3 + 1);
        columnFieldNames.values().forEach(fieldName -> dataRow.put(fieldName, null));
    }

    private enum CellDataType {
        BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER, DATE, NULL
    }

    private static class XmlConstants {
        public static final String TAG_DIMENSION = "dimension";
        public static final String TAG_ROW = "row";
        public static final String TAG_CELL = "c";
        public static final String TAG_VALUE = "v";
        public static final String TAG_FORMULA = "f";
        /**
         * When the data is "inlineStr" his tag is "t"
         */
        public static final String TAG_INLINE_STRING_VALUE = "t";

        public static final String ATTRIBUTE_S = "s";
        public static final String ATTRIBUTE_R = "r";
        public static final String ATTRIBUTE_T = "t";
        public static final String ATTRIBUTE_REF = "ref";
        public static final String ATTRIBUTE_LOCATION = "location";

        public static final String MERGE_CELL_TAG = "mergeCell";
        public static final String HYPERLINK_TAG = "hyperlink";

        public static final String X_DIMENSION_TAG = "x:dimension";
        public static final String X_ROW_TAG = "x:row";
        public static final String X_CELL_FORMULA_TAG = "x:f";
        public static final String X_CELL_VALUE_TAG = "x:v";
        /**
         * When the data is "inlineStr" his tag is "t"
         */
        public static final String X_CELL_INLINE_STRING_VALUE_TAG = "x:t";
        public static final String X_CELL_TAG = "x:c";
        public static final String X_MERGE_CELL_TAG = "x:mergeCell";
        public static final String X_HYPERLINK_TAG = "x:hyperlink";
    }
}
