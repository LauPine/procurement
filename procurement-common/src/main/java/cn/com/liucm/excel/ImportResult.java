package cn.com.liucm.excel;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangql on 2017/3/16.
 */
public class ImportResult {

    private Workbook errorBook;

    private Sheet errorSheet;

    private String importExcelFile;

    private String errrorFile;

    //错误行数
    private Long errorRowNum = Long.valueOf(0);

    //错误行数
    private Long allRowNum = Long.valueOf(0);

    //文件uuid
    private String fileId = "";

    //错误行号
    private Long errorRowIndex = Long.valueOf(0);

    //标题
    private List titleList = new ArrayList();

    //行数据
    private List rowList = new ArrayList();

    public Workbook getErrorBook() {
        return errorBook;
    }

    public void setErrorBook(Workbook errorBook) {
        this.errorBook = errorBook;
    }

    public Sheet getErrorSheet() {
        return errorSheet;
    }

    public void setErrorSheet(Sheet errorSheet) {
        this.errorSheet = errorSheet;
    }

    public String getImportExcelFile() {
        return importExcelFile;
    }

    public void setImportExcelFile(String importExcelFile) {
        this.importExcelFile = importExcelFile;
    }

    public String getErrrorFile() {
        return errrorFile;
    }

    public void setErrrorFile(String errrorFile) {
        this.errrorFile = errrorFile;
    }

    public Long getErrorRowNum() {
        return errorRowNum;
    }

    public void setErrorRowNum(Long errorRowNum) {
        this.errorRowNum = errorRowNum;
    }

    public Long getAllRowNum() {
        return allRowNum;
    }

    public void setAllRowNum(Long allRowNum) {
        this.allRowNum = allRowNum;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Long getErrorRowIndex() {
        return errorRowIndex;
    }

    public void setErrorRowIndex(Long errorRowIndex) {
        this.errorRowIndex = errorRowIndex;
    }

    public List getTitleList() {
        return titleList;
    }

    public void setTitleList(List titleList) {
        this.titleList = titleList;
    }

    public List getRowList() {
        return rowList;
    }

    public void setRowList(List rowList) {
        this.rowList = rowList;
    }
}
