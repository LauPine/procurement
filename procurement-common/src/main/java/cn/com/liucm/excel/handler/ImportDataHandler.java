package cn.com.liucm.excel.handler;

import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel导入数据分析处理类
 *
 * @author wangql on 2014/12/08.
 * @version 1.0
 */
public class ImportDataHandler extends DefaultHandler {

    /** POI SAX中共享字符表. */
    protected SharedStringsTable sst;

    /** sax读到的最新的单元格内容. */
    protected String lastContents;

    /** 是否接下去读的是字符. */
    protected boolean nextIsString;

    /** 有多少列. */
    protected int columnSize;

    /** 列名集合. */
    protected String[] cellColumnNames;

    /** 当前读的列序号. */
    protected int currentColumnIndex;

    /** 当前读的行号. */
    protected int currentRow = -1;

    /** 当前单元格序号，如A1，B2. */
    protected String currentCell;

    /** 行数据. */
    protected List row = new ArrayList();

    /** 表头. */
    protected List title = new ArrayList();

    /** Excel的数据流. */
    private InputStream excelStream;

    /** 是否只读表头. */
    private boolean onlyTitle;

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(ImportDataHandler.class);

    /**
     * Instantiates a new import data handler.
     * 
     * @param onlyTitle
     *            the only title
     * @param excelStream
     *            the excel stream
     * @param sst
     *            the sst
     * @param columnSize
     *            the column size
     */
    public ImportDataHandler(boolean onlyTitle, InputStream excelStream, SharedStringsTable sst, int columnSize) {
        this.excelStream = excelStream;
        this.onlyTitle = onlyTitle;
        this.columnSize = columnSize;
        this.sst = sst;
        initCellColumnNames();
        initRow();
    }

    /**
     * 初始化列名，目前只支持A-Z共26列.
     */
    protected void initCellColumnNames() {
        cellColumnNames = new String[columnSize];
        for (int i = 0; i < columnSize; i++) {
            // 'A'是65
            cellColumnNames[i] = toColumnIndexName(i + 1);
        }
    }

    /**
     * 根据列的数字序号来获取文字序号,如第1列是A，第26列是Z，地27列是AA，第52列是AZ等等.
     *
     * @param number
     *            the number
     * @return the string
     */
    private String toColumnIndexName(int number) {
        StringBuilder sb = new StringBuilder();
        while (number-- > 0) {
            sb.append((char) ('A' + (number % 26)));
            number /= 26;
        }
        return sb.reverse().toString();
    }

    /**
     * 初始化行列表数据，根据所有的列名，赋予控制，否则的话，sax的读取会直接跳过空行，导致顺序乱掉.
     */
    protected void initRow() {
        for (int i = 0; i < cellColumnNames.length; i++) {
            row.add(null);
        }
    }

    /**
     * 是否是空行.
     * 
     * @param row
     *            一行内的数据集合
     * @return true为空行，false表示有数据
     */
    protected boolean isEmptyRow(List row) {
        boolean flag = true;
        for (int i = 0; i < row.size(); i++) {
            Object cellValue = row.get(i);
            flag = flag & (cellValue == null || "".equals(cellValue.toString().trim()));
        }
        return flag;
    }

    /**
     * 重设行读取，表示一行已经读完.
     * 
     * @param lastRow
     *            是否是最后一行
     */
    public void resetRow(boolean lastRow) {
        long time = System.nanoTime();
        if (currentRow == 1) {
            title.addAll(row);
            try {
                if (onlyTitle) {
                    excelStream.close();
                }
            } catch (IOException e) {
                LOG.error("", e);
            }
        }
        if (currentRow >= 1) {
            LOG.debug("行{},数据：{}", currentRow, row.toString());
        }
        row.clear();
        initRow();
        LOG.debug("ImportDataHandler读取每行的时间：" + String.valueOf(System.nanoTime() - time));
    }

    /**
     * 根据列名（表头）来得到所在的数字序号.
     * 
     * @param columnName
     *            the column name
     * @return the column index
     */
    private int getColumnIndex(String columnName) {
        for (int i = 0; i < cellColumnNames.length; i++) {
            if (columnName.equals(cellColumnNames[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        // c => cell单元格
        if ("c".equals(name)) {
            currentCell = attributes.getValue("r");
            // 只支持最大A-Z 26列导入
            int rowIndex;
            try {
                rowIndex = Integer.parseInt(currentCell.substring(1));

                String column = currentCell.substring(0, 1);
                currentColumnIndex = getColumnIndex(column);
                if (rowIndex != currentRow) {
                    resetRow(false);
                    currentRow = rowIndex;
                }
                String cellType = attributes.getValue("t");
                if ("s".equals(cellType)) {
                    nextIsString = true;
                } else {
                    nextIsString = false;
                }
            } catch (NumberFormatException e) {
                LOG.warn("超出26列最大限制");
            }
        }
        lastContents = "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        // 先要拿到数据
        if (nextIsString) {
            int idx = Integer.parseInt(lastContents);
            lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString().trim();
            if ("".equals(lastContents)) {
                lastContents = null;
            }
            nextIsString = false;
        }

        // v => 单元格中值所在之处
        if ("v".equals(name)) {
            // System.out.println(lastContents);
            if (currentColumnIndex == -1) {
                LOG.warn("模板范围外仍有数据，行{}列{},内容{}", currentRow, currentCell, lastContents);
            } else {
                row.set(currentColumnIndex, lastContents);
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        lastContents += new String(ch, start, length);
    }

    /**
     * Gets the row.
     * 
     * @return the row
     */
    public List getRow() {
        return row;
    }

    /**
     * Gets the title.
     * 
     * @return the title
     */
    public List getTitle() {
        return title;
    }

    /**
     * Gets the current cell.
     * 
     * @return the current cell
     */
    public String getCurrentCell() {
        return currentCell;
    }

    /**
     * Gets the current row.
     * 
     * @return the current row
     */
    public int getCurrentRow() {
        return currentRow;
    }

    /**
     * Gets the current column index.
     * 
     * @return the current column index
     */
    public int getCurrentColumnIndex() {
        return currentColumnIndex;
    }

    /**
     * Gets the cell column names.
     * 
     * @return the cell column names
     */
    public String[] getCellColumnNames() {
        return cellColumnNames;
    }

    /**
     * Gets the column size.
     * 
     * @return the column size
     */
    public int getColumnSize() {
        return columnSize;
    }
}
