package cn.com.liucm.excel.handler;

import cn.com.liucm.excel.RowObject;
import org.apache.poi.xssf.model.SharedStringsTable;

import java.io.InputStream;
import java.util.List;


/**
 * 批量读取的poi sax非空行处理对象
 *
 * Created by Qil.Wong on 2016/3/9.
 */
public class RowReadHandler extends ImportDataHandler {

    /** 读取一行后的处理对象. */
    private RowReadAction rowReadAction;

    /**
     * Instantiates a new row read handler.
     * 
     * @param onlyTitle
     *            the only title
     * @param sheet2
     *            the sheet2
     * @param sst
     *            the sst
     * @param columnSize
     *            the column size
     */
    public RowReadHandler(boolean onlyTitle, InputStream sheet2, SharedStringsTable sst, int columnSize) {
        super(onlyTitle, sheet2, sst, columnSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetRow(boolean lastRow) {
        if (currentRow > 1) { // 除标题外
            List row = getRow();
            // if(!isEmptyRow(row)) {
            // RowObject rowObject = new RowObject();
            // rowObject.setRowIndex(currentRow);
            // rowObject.addAll(row);
            // rowReadAction.readRow(rowObject,lastRow);
            // }
            RowObject rowObject = new RowObject();
            rowObject.setRowIndex(currentRow);
            rowObject.addAll(row);
            rowReadAction.readRow(rowObject, lastRow);
        }else if(currentRow == 1){//读取标题
            rowReadAction.setTitle(title);
        }
        super.resetRow(lastRow);
    }

    /**
     * 设置读行的动作，每一行读完后，开始执行这个Action的readRow(...)方法
     * 
     * @param rowReadAction
     *            the new row read action
     */
    public void setRowReadAction(RowReadAction rowReadAction) {
        this.rowReadAction = rowReadAction;
    }
}
