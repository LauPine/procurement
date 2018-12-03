package cn.com.liucm.excel;

import cn.com.liucm.exception.ImportFileReadException;

/**
 * 文件导入接口
 * Created by liucm on 2017/3/17.
 */
public interface Importer {

    void importFile(String importExcelFile)  throws ImportFileReadException;
}
