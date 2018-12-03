package cn.com.liucm.excel.handler;

import cn.com.liucm.excel.RowObject;

import java.util.List;

/**
 * 读行后的动作接口
 * Created by liucm on 2014/12/9.
 */
public interface RowReadAction {

    /**
     * Read row.
     * 
     * @param rawRowObject
     *            the raw row object
     * @param lastRow
     *            the last row
     */
    public void readRow(RowObject rawRowObject, boolean lastRow);

    /**
     * 设置标题
     */
    public void setTitle(List row);
}
