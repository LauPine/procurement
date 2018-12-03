package cn.com.liucm.excel;

import cn.com.liucm.excel.handler.RowReadAction;
import cn.com.liucm.excel.handler.RowReadHandler;
import cn.com.liucm.exception.ImportFileReadException;
import cn.com.liucm.exception.ProcurementServiceException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Excel 2007+版(.xlsx)通过sax parser进行解析导入。
 * 线程安全
 * Created by liucm on 2017/3/13.
 */
public abstract class SaxImporter implements RowReadAction, Importer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SaxImporter.class);

    protected static ThreadLocal<ImportResult> importResultLocal = new ThreadLocal<ImportResult>();

    public SaxImporter() {

    }

    @Override
    public void importFile(String importExcelFile) throws ImportFileReadException {
        ImportResult importResult = new ImportResult();
        importResult.setImportExcelFile(importExcelFile);
        importResultLocal.set(importResult);
        boolean onlyTitle = false;
        try {
            OPCPackage pkg = OPCPackage.open(importExcelFile);
            XSSFReader r = new XSSFReader(pkg);
            // 此处只读第一页
            InputStream sheet2 = r.getSheet("rId1");
            SharedStringsTable sst = r.getSharedStringsTable();
            XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
            RowReadHandler handler = new RowReadHandler(onlyTitle, sheet2, sst, getColumnNames().length);
            handler.setRowReadAction(this);
            parser.setContentHandler(handler);
            InputSource sheetSource = new InputSource(sheet2);
            parser.parse(sheetSource);
            handler.resetRow(true);
            sheet2.close();
            pkg.close();
        } catch (Exception e) {
            if (!onlyTitle || "Premature end of file".equals(e.getMessage())) {
                LOGGER.error(importExcelFile + "导入过程产生错误", e);
//                writeErrorRow(importResult, importExcelFile + " 导入过程产生错误!" + e.getMessage());
                throw new ImportFileReadException();
            } else {
                LOGGER.debug(e.getMessage());
            }
        } finally {
            saveErrorBook(importResult);
        }
    }
    private String[] errorTitle = new String[] {"错误信息"};
    /**
     * 写错误内容
     * 
     * @param cellObjects
     */
    protected void writeErrorRow(ImportResult importResult, Object... cellObjects) {
        if (cellObjects == null) {
            return;
        }
        int indexRow = Integer.valueOf(String.valueOf(importResult.getErrorRowIndex()));
        try {
            if (importResult.getErrorSheet() == null) {
                importResult.setErrrorFile(importResult.getImportExcelFile().replaceAll(".xlsx", "_RET.xlsx"));
                importResult.setErrorBook(new SXSSFWorkbook(1000));
                importResult.setErrorSheet(importResult.getErrorBook().createSheet());
                importResultLocal.set(importResult);
                Row row = importResult.getErrorSheet().createRow(0);
                List titleList = importResultLocal.get().getTitleList();
                //循环标题
                int i = 0;
                for(; i < titleList.size(); i++){
                    Cell cell = row.createCell(i);
                    cell.setCellType(CellType.STRING);// 文本格式
                    cell.setCellValue((String) titleList.get(i));// 写入内容
                }
                i++;
                //循环行号和原因
                for(; i < cellObjects.length + titleList.size() + 1; i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellType(CellType.STRING);// 文本格式
                    cell.setCellValue(errorTitle[i - titleList.size()-1]);// 写入内容
                }
            }
//            int columnSize = cellObjects.length;
//            for (Object celObject : cellObjects) {
//                Row row = importResult.getErrorSheet().createRow(indexRow);
//                for (int i = 0; i < columnSize; i++) {
//                    Cell cell = row.createCell(i);
//                    cell.setCellType(CellType.STRING);// 文本格式
//                    cell.setCellValue(celObject == null ? "" : celObject.toString());// 写入内容
//                }
//                indexRow++;
//            }
            Row row = importResult.getErrorSheet().createRow(indexRow);
            List rowList = importResultLocal.get().getRowList();
            int i = 0;
            for(; i < rowList.size(); i++){
                Cell cell = row.createCell(i);
                cell.setCellType(CellType.STRING);// 文本格式
                cell.setCellValue((String) rowList.get(i));// 写入内容
            }
            i++;
            for (; i < cellObjects.length + rowList.size() + 1; i++) {
                Object celObject = cellObjects[i - rowList.size()-1];
                Cell cell = row.createCell(i);
                cell.setCellType(CellType.STRING);// 文本格式
                cell.setCellValue(celObject == null ? "" : celObject.toString());// 写入内容
            }
        } catch (Exception e) {
            throw new ProcurementServiceException(e);
        }
    }

    /**
     * 保存错误内容至excel文件
     */
    protected void saveErrorBook(ImportResult importResult) {
        if (importResult.getErrorSheet() == null) {
            return;
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(importResult.getErrrorFile());
            importResult.getErrorBook().write(fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            throw new ProcurementServiceException(e);
        } finally {
            ((SXSSFWorkbook) importResult.getErrorBook()).dispose();// 清除临时文件
            try {
                importResult.getErrorBook().close();
            } catch (IOException e) {
                throw new ProcurementServiceException(e);
            }
        }
    }

    /**
     * 指定导入模板有多少列
     * 
     * @return 列数
     */
    protected abstract String[] getColumnNames();

}
