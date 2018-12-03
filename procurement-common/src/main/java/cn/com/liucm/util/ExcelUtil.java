package cn.com.liucm.util;

import cn.com.liucm.constant.ExceptionConstants;
import cn.com.liucm.exception.ProcurementServiceException;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.*;
import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFRenderer;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtil {
    /**
     * 获取图片和位置 (xlsx)
     * @param sheet
     * @return
     * @throws IOException
     */
    public static Map<String, XSSFPictureData> getPictures (XSSFSheet sheet){
        Map<String, XSSFPictureData> map = new HashMap<String, XSSFPictureData>();
        List<POIXMLDocumentPart> list = sheet.getRelations();
        for (POIXMLDocumentPart part : list) {
            if (part instanceof XSSFDrawing) {
                XSSFDrawing drawing = (XSSFDrawing) part;
                List<XSSFShape> shapes = drawing.getShapes();
                for (XSSFShape shape : shapes) {
                    XSSFPicture picture = (XSSFPicture) shape;
                    XSSFClientAnchor anchor = picture.getPreferredSize();
                    CTMarker marker = anchor.getFrom();
                    String key = marker.getRow() + "-" + marker.getCol();
                    map.put(key, picture.getPictureData());
                }
            }
        }
        return map;
    }

    public static void savePicture(String path, XSSFPictureData pic){
        //String ext = pic.suggestFileExtension();
        byte[] data = pic.getData();
        //如果是emf格式的
        if("image/x-emf".equals(pic.getMimeType())){
            path = path.substring(0, path.lastIndexOf(".")) + ".emf";
        }
        if("image/x-wmf".equals(pic.getMimeType())){
            path = path.substring(0, path.lastIndexOf(".")) + ".wmf";
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            out.write(data);
            out.close();
//            if("image/x-emf".equals(pic.getMimeType())){
//                InputStream inputStream = new FileInputStream(path);
//                emfToPng(inputStream);
//            }
        } catch (FileNotFoundException e) {
            throw new ProcurementServiceException(e);
        } catch (IOException e) {
            throw new ProcurementServiceException(e);
        }
    }

    /**
     * 将emf文件类型转换成png
     * @param is
     * @return
     */
    private static byte[] emfToPng(InputStream is){
        byte[] by=null;
        EMFInputStream emf = null;
        EMFRenderer emfRenderer = null;
        //创建储存图片二进制流的输出流
        ByteArrayOutputStream baos = null;
        //创建ImageOutputStream流
        ImageOutputStream imageOutputStream = null;
        try {
            emf = new EMFInputStream(is, EMFInputStream.DEFAULT_VERSION);
            emfRenderer = new EMFRenderer(emf);
            final int width = (int)emf.readHeader().getBounds().getWidth();
            final int height = (int)emf.readHeader().getBounds().getHeight();
            final BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D)result.createGraphics();
            emfRenderer.paint(g2);
            //创建储存图片二进制流的输出流
            baos = new ByteArrayOutputStream();
            //创建ImageOutputStream流
            imageOutputStream = ImageIO.createImageOutputStream(baos);
            //将二进制数据写进ByteArrayOutputStream
            ImageIO.write(result, "png", imageOutputStream);
            by=baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try {
                if(imageOutputStream!=null){
                    imageOutputStream.close();
                }
                if(baos!=null){
                    baos.close();
                }
                if(emfRenderer!=null){
                    emfRenderer.closeFigure();
                }
                if(emf!=null){
                    emf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return by;
    }


    public static void exportAllInfo(HttpServletResponse response, List<List<Map<String,Object>>> dataAllList, List<String[]> keysAllList, List<String[]> namesAllList, String exportFileName){
        // 设置response参数，可以打开下载页面
        response.reset();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ExcelUtil.createWorkBook(dataAllList, keysAllList, namesAllList).write(os);
        } catch (IOException e) {
            throw new ProcurementServiceException(ExceptionConstants.RECORD_EXPORT_ERROR);
        }
        byte[] content = os.toByteArray();
        InputStream is = new ByteArrayInputStream(content);
        try {
            response.setHeader("Content-Disposition", "attachment;filename="+ new String((exportFileName + ".xlsx").getBytes(), "iso8859-1"));
        } catch (UnsupportedEncodingException e) {
            throw new ProcurementServiceException(ExceptionConstants.RECORD_EXPORT_ERROR);
        }
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
        } catch (IOException e) {
            throw new ProcurementServiceException(ExceptionConstants.RECORD_EXPORT_ERROR);
        }
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(out);
            byte[] buff = new byte[2048];
            int bytesRead;
            // Simple read/write loop.
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (final IOException e) {
            throw new ProcurementServiceException(ExceptionConstants.RECORD_EXPORT_ERROR);
        } finally {
            if (bis != null)
                try {
                    bis.close();
                } catch (IOException e) {
                    throw new ProcurementServiceException(ExceptionConstants.RECORD_EXPORT_ERROR);
                }
            if (bos != null)
                try {
                    bos.close();
                } catch (IOException e) {
                    throw new ProcurementServiceException(ExceptionConstants.RECORD_EXPORT_ERROR);
                }
        }
    }

    /**
     * 创建excel文档，
     * [@param](http://my.oschina.net/u/2303379) list 数据
     * @param keysList list中map的key数组集合
     * @param columnNamesList excel的列名集合
     * */
    public static Workbook createWorkBook(List<List<Map<String, Object>>> sheetList, List<String[]> keysList, List<String[]> columnNamesList) {
        // 创建excel工作簿
        Workbook wb = new XSSFWorkbook();
        // 创建第一个sheet（页），并命名
        for(int m = 0; m < sheetList.size(); m++) {
            List<Map<String, Object>> list = sheetList.get(m);
            Sheet sheet = wb.createSheet(list.get(0).get("sheetName").toString());
            Drawing patriarch = sheet.createDrawingPatriarch();



            // 手动设置列宽。第一个参数表示要为第几列设；，第二个参数表示列的宽度，n为列高的像素数。
            for(int i=0; i < keysList.get(m).length;i++){
                sheet.setColumnWidth((short) i, (short) (35.7 * 150));
            }
            // 创建第一行
            Row row = sheet.createRow((short) 0);

            // 创建两种单元格格式
            CellStyle cs = wb.createCellStyle();
            CellStyle cs2 = wb.createCellStyle();

            // 创建两种字体
            Font f = wb.createFont();
            Font f2 = wb.createFont();

            // 创建第一种字体样式（用于列名）
            f.setFontHeightInPoints((short) 10);
            f.setColor(IndexedColors.BLACK.getIndex());
            f.setBoldweight(Font.BOLDWEIGHT_BOLD);

            // 创建第二种字体样式（用于值）
            f2.setFontHeightInPoints((short) 10);
            f2.setColor(IndexedColors.BLACK.getIndex());

            // 设置第一种单元格的样式（用于列名）
            cs.setFont(f);
            cs.setBorderLeft(CellStyle.BORDER_THIN);
            cs.setBorderRight(CellStyle.BORDER_THIN);
            cs.setBorderTop(CellStyle.BORDER_THIN);
            cs.setBorderBottom(CellStyle.BORDER_THIN);
            cs.setAlignment(CellStyle.ALIGN_CENTER);

            // 设置第二种单元格的样式（用于值）
            cs2.setFont(f2);
            cs2.setBorderLeft(CellStyle.BORDER_THIN);
            cs2.setBorderRight(CellStyle.BORDER_THIN);
            cs2.setBorderTop(CellStyle.BORDER_THIN);
            cs2.setBorderBottom(CellStyle.BORDER_THIN);
            cs2.setAlignment(CellStyle.ALIGN_CENTER);
            //设置列名
            for(int i=0;i<columnNamesList.get(m).length;i++){
                Cell cell = row.createCell(i);
                cell.setCellValue(columnNamesList.get(m)[i]);
                cell.setCellStyle(cs);
            }
            //设置每行每列的值
            for (short i = 1; i < list.size(); i++) {
                // Row 行,Cell 方格 , Row 和 Cell 都是从0开始计数的
                // 创建一行，在页sheet上
                Row row1 = sheet.createRow((short) i);
                row1.setHeight((short)(160 * 15.625));
                // 在row行上创建一个方格
                for(short j=0;j<keysList.get(m).length;j++){
                    Cell cell = row1.createCell(j);
                    cell.setCellStyle(cs2);

                    if(cell.getColumnIndex() != 4){
                        cell.setCellValue(list.get(i).get(keysList.get(m)[j]) == null ? " ": list.get(i).get(keysList.get(m)[j]).toString());
                    }else if(cell.getColumnIndex() == 4){
                        try {
                            String fileName = list.get(i).get(keysList.get(m)[j]).toString();
                            if(!StringUtil.isNullString(fileName)){
                                if(fileName.indexOf(".png") == -1){
                                    throw new ProcurementServiceException();
                                }
                            }
                            InputStream is = null;
                            byte[] bytes = null;
                            is = new FileInputStream(fileName);
                            bytes = IOUtils.toByteArray(is);
                            int pictureIdx = wb.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
                            CreationHelper helper = wb.getCreationHelper();
                            Drawing drawing = sheet.createDrawingPatriarch();
                            ClientAnchor anchor = helper.createClientAnchor();
                            // 图片插入坐标
                            anchor.setCol1(cell.getColumnIndex());
                            anchor.setRow1(cell.getRowIndex());
                            // 指定我想要的长宽
                            double standardWidth = 160;
                            double standardHeight = 100;
                            // 计算单元格的长宽
                            double cellWidth = sheet.getColumnWidthInPixels(cell.getColumnIndex());
                            double cellHeight = cell.getRow().getHeightInPoints() / 72 * 96;
                            // 计算需要的长宽比例的系数
                            double a = standardWidth / cellWidth;
                            double b = standardHeight / cellHeight;
                            // 插入图片
                            Picture pict = drawing.createPicture(anchor, pictureIdx);
                            pict.resize(a, b);
                        } catch (FileNotFoundException e) {
                            cell = row1.createCell(j);
                            cell.setCellStyle(cs2);
                            cell.setCellValue("图片不存在!!!!!!!!!");
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e){//当图片不存在或者不能解析则填入空白
                            cell = row1.createCell(j);
                            cell.setCellStyle(cs2);
                            cell.setCellValue("图片无法解析或不存在图片!!!!!!!!!");
                        }
                    }

                }
            }
        }
        return wb;
    }
}
