package cn.com.liucm.service.impl;

import cn.com.liucm.dto.PurchaseInfoDto;
import cn.com.liucm.excel.ImportResult;
import cn.com.liucm.excel.RowObject;
import cn.com.liucm.excel.SaxImporter;
import cn.com.liucm.exception.ImportFileReadException;
import cn.com.liucm.exception.ProcurementImportException;
import cn.com.liucm.exception.ProcurementServiceException;
import cn.com.liucm.exception.ProcurementValidatorException;
import cn.com.liucm.mapper.PurchaseInfoMapper;
import cn.com.liucm.support.MessageSupport;
import cn.com.liucm.util.ExcelUtil;
import cn.com.liucm.util.StringUtil;
import cn.com.liucm.util.UUIDGeneratorUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 采购信息导入处理类
 * Created by liucm on 2017/3/13.
 */
@Component
public class PurchaseInfoImporter extends SaxImporter {
    private final static Logger LOGGER = LoggerFactory.getLogger(PurchaseInfoImporter.class);
    @Value("${image.upload.location}")
    private String imageUploadLocation;

    @Autowired
    private MessageSupport messageSupport;

    @Autowired
    private PurchaseInfoMapper purchaseInfoMapper;

    @Override
    protected String[] getColumnNames() {
        /**
         CREATE TABLE `purchaseinfo` (
         `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增序列',
         `companyNo` varchar(10) DEFAULT NULL COMMENT '公司编号',
         `picturePath` varchar(2000) DEFAULT NULL COMMENT '产品图片路径',
         `param` varchar(100) DEFAULT NULL COMMENT '产品参数',
         `showOEM` varchar(2000) DEFAULT NULL COMMENT '显示的OEM',
         `num` int(11) DEFAULT NULL COMMENT '数量',
         `buyUnitPrice` decimal(20,2) DEFAULT NULL COMMENT '采购单价',
         `buyTotalPrice` decimal(20,2) DEFAULT NULL COMMENT '采购总价',
         `supplier` varchar(100) DEFAULT NULL COMMENT '供应商',
         `saleUnitPrice` decimal(20,2) DEFAULT NULL COMMENT '销售单价',
         `saleTotalPrice` decimal(20,2) DEFAULT NULL COMMENT '销售总价',
         `rate` decimal(20,2) DEFAULT NULL COMMENT '利率',
         `packType` varchar(100) DEFAULT NULL COMMENT '包装方式',
         `eachPackNum` int(11) DEFAULT NULL COMMENT '每件数量',
         `packNum` int(11) DEFAULT NULL COMMENT '件数',
         `sumNetWeight` decimal(20,2) DEFAULT NULL COMMENT '总净重',
         `sumGrossWeight` decimal(20,2) DEFAULT NULL COMMENT '总毛重',
         `volume` decimal(20,2) DEFAULT NULL COMMENT '体积',
         `size` varchar(100) DEFAULT NULL COMMENT '尺寸',
         `netWeight` decimal(20,2) DEFAULT NULL COMMENT '净重',
         `grossWeight` decimal(20,2) DEFAULT NULL COMMENT '毛重',
         `spareSupplier1` varchar(100) DEFAULT NULL COMMENT '备用供应商1',
         `spareSupplier1BuyPrice` decimal(20,2) DEFAULT NULL COMMENT '备用供应商1采购价',
         `spareSupplier2` varchar(100) DEFAULT NULL COMMENT '备用供应商2',
         `spareSupplier2BuyPrice` decimal(20,2) DEFAULT NULL COMMENT '备用供应商2采购价',
         `spareSupplier3` varchar(100) DEFAULT NULL COMMENT '备用供应商3',
         `spareSupplier3BuyPrice` decimal(20,2) DEFAULT NULL COMMENT '备用供应商3采购价',
         `AS` varchar(8000) DEFAULT NULL COMMENT 'AS',
         `WOODAUTO` varchar(8000) DEFAULT NULL COMMENT 'WOODAUTO',
         `supplierList` varchar(500) DEFAULT NULL COMMENT '供应商价格列表',
         PRIMARY KEY (`id`)
         ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
         */
        return new String[] { "companyNo", "picturePath", "param", "showOEM", "num", "buyPrice", "money", "defaultSupplier"
                , "packType", "eachPackNum", "packNum", "sumNetWeight", "sumGrossWeight", "volume", "size", "netWeight",
                "grossWeight", "spareSupplier1", "spareSupplier2", "spareSupplier3", "AS", "WOODAUTO"};
    }

    public void importFile(String importExcelFile)  {
        try {
            super.importFile(importExcelFile);
        } catch (ImportFileReadException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void readRow(RowObject rawRowObject, boolean lastRow) {
        try{
            importResultLocal.get().setRowList(rawRowObject.getRow());
            importResultLocal.get().setAllRowNum(importResultLocal.get().getAllRowNum() + 1);
            addPurchaseInfoImport(rawRowObject);
        }catch (ProcurementImportException e){
            LOGGER.debug("导入信息错误:" + messageSupport.getMessage(e.getMessage()),e);
            importResultLocal.get().setErrorRowNum(importResultLocal.get().getErrorRowNum() + 1);
            ImportResult importResult = importResultLocal.get();
            importResult.setErrorRowIndex(importResultLocal.get().getErrorRowNum());
            importResult.setFileId(importResultLocal.get().getFileId());
            super.writeErrorRow(importResult, messageSupport.getMessage(e.getMessage()));
        }catch (ProcurementValidatorException e){
            JSONObject obj = JSON.parseObject(e.getMessage());
            String failReason = obj.getString("failReason");
            JSONArray lxrSuccessIndexArray = obj.getJSONArray("lxrSuccessIndex");
            LOGGER.debug("导入信息错误:" + failReason);
            importResultLocal.get().setErrorRowNum(importResultLocal.get().getErrorRowNum() + 1);
            ImportResult importResult = importResultLocal.get();
            for(Object object: lxrSuccessIndexArray){
                String lxrSuccessIndex = (String)object;
                if("1".equals(lxrSuccessIndex)){
                    for(int i = 9; i < 14; i++) {
                        importResult.getRowList().set(i, "");
                    }
                }
                if("2".equals(lxrSuccessIndex)){
                    for(int i = 14; i < 19; i++) {
                        importResult.getRowList().set(i, "");
                    }
                }
                if("3".equals(lxrSuccessIndex)){
                    for(int i = 19; i < 24; i++) {
                        importResult.getRowList().set(i, "");
                    }
                }
            }
            importResult.setErrorRowIndex(importResultLocal.get().getErrorRowNum());
            importResult.setFileId(importResultLocal.get().getFileId());
            super.writeErrorRow(importResult, failReason);
        }catch (Exception e){
            LOGGER.debug("导入异常" ,e);
        }
    }

    @Transactional
    public void addPurchaseInfoImport(RowObject rawRowObject) throws ProcurementImportException {
        PurchaseInfoDto purchaseInfoDto = this.getPurchaseInfoDto(rawRowObject.getRow());
        try {
            purchaseInfoMapper.addPurchaseInfo(purchaseInfoDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setTitle(List row) {

    }

    private PurchaseInfoDto getPurchaseInfoDto(List purchaseInfoList){
        PurchaseInfoDto purchaseInfoDto = new PurchaseInfoDto();
        purchaseInfoDto.setCompanyNo(purchaseInfoList.get(0) == null ? "" : (String) purchaseInfoList.get(0));
        purchaseInfoDto.setPicturePath(purchaseInfoList.get(1) == null ? "" : (String) purchaseInfoList.get(1));
        purchaseInfoDto.setParam(purchaseInfoList.get(2) == null ? "" : (String) purchaseInfoList.get(2));
        purchaseInfoDto.setShowOEM(purchaseInfoList.get(3) == null ? "" : (String) purchaseInfoList.get(3));
        purchaseInfoDto.setNum(purchaseInfoList.get(4) == null ? Integer.valueOf(0) : Integer.valueOf((String) purchaseInfoList.get(4)));
//        purchaseInfoDto.setBuyPrice(purchaseInfoList.get(5) == null ? BigDecimal.ZERO : new BigDecimal((String) purchaseInfoList.get(5)));
//        purchaseInfoDto.setMoney(purchaseInfoList.get(6) == null ? BigDecimal.ZERO : new BigDecimal((String) purchaseInfoList.get(6)));
//        purchaseInfoDto.setDefaultSupplier(purchaseInfoList.get(7) == null ? "" : (String) purchaseInfoList.get(7));
        purchaseInfoDto.setPackType(purchaseInfoList.get(8) == null ? "" : (String) purchaseInfoList.get(8));
        purchaseInfoDto.setEachPackNum(purchaseInfoList.get(9) == null ? Integer.valueOf(0) : Integer.valueOf((String) purchaseInfoList.get(9)));
        purchaseInfoDto.setPackNum(purchaseInfoList.get(10) == null ? Integer.valueOf(0) : Integer.valueOf((String) purchaseInfoList.get(10)));
        purchaseInfoDto.setSumNetWeight(purchaseInfoList.get(11) == null ? BigDecimal.ZERO : new BigDecimal((String) purchaseInfoList.get(11)));
        purchaseInfoDto.setSumGrossWeight(purchaseInfoList.get(12) == null ? BigDecimal.ZERO : new BigDecimal((String) purchaseInfoList.get(12)));
        purchaseInfoDto.setVolume(purchaseInfoList.get(13) == null ? BigDecimal.ZERO : new BigDecimal((String) purchaseInfoList.get(13)));
        purchaseInfoDto.setSize(purchaseInfoList.get(14) == null ? "" : (String) purchaseInfoList.get(14));
        purchaseInfoDto.setNetWeight(purchaseInfoList.get(15) == null ? BigDecimal.ZERO : new BigDecimal((String) purchaseInfoList.get(15)));
        purchaseInfoDto.setGrossWeight(purchaseInfoList.get(16) == null ? BigDecimal.ZERO : new BigDecimal((String) purchaseInfoList.get(16)));
        purchaseInfoDto.setSpareSupplier1(purchaseInfoList.get(17) == null ? "" : (String) purchaseInfoList.get(17));
        purchaseInfoDto.setSpareSupplier2(purchaseInfoList.get(18) == null ? "" : (String) purchaseInfoList.get(18));
        purchaseInfoDto.setSpareSupplier3(purchaseInfoList.get(19) == null ? "" : (String) purchaseInfoList.get(19));
        purchaseInfoDto.setAS(purchaseInfoList.get(20) == null ? "" : (String) purchaseInfoList.get(20));
        purchaseInfoDto.setWOODAUTO(purchaseInfoList.get(21) == null ? "" : (String) purchaseInfoList.get(21));
        return purchaseInfoDto;
    }

    @Transactional(rollbackFor = Exception.class)
    public void importPictureFile(String importExcelFile, MultipartFile file)  {
        File newFile = new File(importExcelFile);
        // 通过CommonsMultipartFile的方法直接写文件（注意这个时候）
        try {
            file.transferTo(newFile);
        } catch (IOException e) {
            throw new ProcurementServiceException(e);
        }
        int rowIndex = 0;
        try {
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(importExcelFile));
            XSSFSheet sheet = wb.getSheetAt(0);
            //读取图片和位置
            Map<String, XSSFPictureData> pictureMap = ExcelUtil.getPictures(sheet);
            for (Row row : sheet) {
                PurchaseInfoDto purchaseInfoDto = new PurchaseInfoDto();
                XSSFPictureData xSSFPictureData = null;
                String path = "";
                rowIndex = 0;
                for (Cell cell : row) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    rowIndex = cell.getRowIndex();
                    //忽略标题行
                    if(rowIndex == 0){
                        break;
                    }
                    if(cell.getColumnIndex() == 0){
                        purchaseInfoDto.setCompanyNo(cell.getRichStringCellValue().getString());
                        int count = purchaseInfoMapper.countPurchaseInfo(purchaseInfoDto.getCompanyNo());
                        if(count > 0){
                            throw new ProcurementServiceException("=============导入时，公司编号：" + purchaseInfoDto.getCompanyNo() + "已经存在！");
                        }
                    }
                    if(cell.getColumnIndex() == 1){
                        String pictureKey = rowIndex + "-" + cell.getColumnIndex();
                        xSSFPictureData = pictureMap.get(pictureKey);
                        if(xSSFPictureData != null){
                            path = imageUploadLocation + purchaseInfoDto.getCompanyNo() + "_" + UUIDGeneratorUtil.getUUID() + "_" + new Date().getTime() + ".png";
//                            if("image/x-emf".equals(xSSFPictureData.getMimeType())){
//                                path = imageUploadLocation + UUIDGeneratorUtil.getUUID() + "_" + new Date().getTime() + ".emf";
//                            }
                            purchaseInfoDto.setPicturePath(path);
                        }
                    }
                    if(cell.getColumnIndex() == 2){
                        purchaseInfoDto.setParam(cell.getRichStringCellValue().getString());
                    }
                    if(cell.getColumnIndex() == 3){
                        purchaseInfoDto.setShowOEM(cell.getRichStringCellValue().getString());
                    }
                    if(cell.getColumnIndex() == 4){
                        purchaseInfoDto.setNum(this.getIntegerFromString(cell.getRichStringCellValue().getString()));
                    }
                    if(cell.getColumnIndex() == 5){
                        purchaseInfoDto.setBuyUnitPrice(this.getBigDecimalFromString(cell.getRichStringCellValue().getString()));
                    }
                    if(cell.getColumnIndex() == 6){
                        //purchaseInfoDto.setBuyTotalPrice(this.getBigDecimalFromString(cell.getRichStringCellValue().getString()));
                        //采购总价=数量*采购单价
                        //purchaseInfoDto.setBuyTotalPrice(new BigDecimal(purchaseInfoDto.getNum()).multiply(purchaseInfoDto.getBuyUnitPrice()));
                    }
                    if(cell.getColumnIndex() == 7){
                        purchaseInfoDto.setSupplier(cell.getRichStringCellValue().getString());
                    }
                    if(cell.getColumnIndex() == 8){
                        purchaseInfoDto.setSaleUnitPrice(this.getBigDecimalFromString(cell.getRichStringCellValue().getString()));
                    }
                    if(cell.getColumnIndex() == 9){
                        purchaseInfoDto.setSaleTotalPrice(this.getBigDecimalFromString(cell.getRichStringCellValue().getString()));
                    }
                    if(cell.getColumnIndex() == 10){
                        //purchaseInfoDto.setRate(this.getBigDecimalFromString(cell.getRichStringCellValue().getString()));
                    }
                    if(cell.getColumnIndex() == 11){
                        //purchaseInfoDto.setPackType(StringUtil.isNullString(cell.getRichStringCellValue().getString()) ? "zxbz" : cell.getRichStringCellValue().getString());
                    }
                    if(cell.getColumnIndex() == 12){
                        purchaseInfoDto.setEachPackNum(this.getIntegerFromString(cell.getRichStringCellValue().getString()));
                    }
                    if(cell.getColumnIndex() == 13){
                        //purchaseInfoDto.setPackNum(this.getIntegerFromString(cell.getRichStringCellValue().getString()));
                        //件数=数量/每件数量
                        //purchaseInfoDto.setPackNum((new BigDecimal(purchaseInfoDto.getNum()).divide(new BigDecimal(purchaseInfoDto.getEachPackNum()))).intValue());
                    }
                    if(cell.getColumnIndex() == 14){
                        //purchaseInfoDto.setSumNetWeight(this.getBigDecimalFromString(cell.getRichStringCellValue().getString()));
                        //总净重=数量*净重
                        //purchaseInfoDto.setSumNetWeight(new BigDecimal(purchaseInfoDto.getNum()).multiply(purchaseInfoDto.getNetWeight()));
                    }
                    if(cell.getColumnIndex() == 15){
                        //purchaseInfoDto.setSumGrossWeight(this.getBigDecimalFromString(cell.getRichStringCellValue().getString()));
                        //总毛重=数量*毛重
                        //purchaseInfoDto.setSumGrossWeight(new BigDecimal(purchaseInfoDto.getNum()).multiply(purchaseInfoDto.getGrossWeight()));
                    }
                    if(cell.getColumnIndex() == 16){
                        //purchaseInfoDto.setVolume(this.getBigDecimalFromString(cell.getRichStringCellValue().getString()));
                        //体积m3
//                        String[] sizeArray = purchaseInfoDto.getSize().split("\\*");
//                        BigDecimal volume = BigDecimal.ONE;
//                        for(String sizeTemp : sizeArray){
//                            volume = volume.multiply(new BigDecimal(sizeTemp));
//                        }
//                        purchaseInfoDto.setVolume(volume);
                    }
                    if(cell.getColumnIndex() == 17){
                        purchaseInfoDto.setSize(cell.getRichStringCellValue().getString());
                        //体积m3
                        String[] sizeArray = purchaseInfoDto.getSize().split("\\*");
                        BigDecimal volume = BigDecimal.ONE;
                        for(String sizeTemp : sizeArray){
                            volume = volume.multiply(new BigDecimal(sizeTemp));
                        }
                        purchaseInfoDto.setVolume(volume.divide(new BigDecimal(1000000)));
                    }
                    if(cell.getColumnIndex() == 18){
                        purchaseInfoDto.setNetWeight(this.getBigDecimalFromString(cell.getRichStringCellValue().getString()));
                    }
                    if(cell.getColumnIndex() == 19){
                        purchaseInfoDto.setGrossWeight(this.getBigDecimalFromString(cell.getRichStringCellValue().getString()));
                    }
                    if(cell.getColumnIndex() == 20){
                        purchaseInfoDto.setSpareSupplier1(cell.getRichStringCellValue().getString());
                    }
                    if(cell.getColumnIndex() == 21){
                        purchaseInfoDto.setSpareSupplier1BuyPrice(this.getBigDecimalFromString(cell.getRichStringCellValue().getString()));
                    }
                    if(cell.getColumnIndex() == 22){
                        purchaseInfoDto.setSpareSupplier2(cell.getRichStringCellValue().getString());
                    }
                    if(cell.getColumnIndex() == 23){
                        purchaseInfoDto.setSpareSupplier2BuyPrice(this.getBigDecimalFromString(cell.getRichStringCellValue().getString()));
                    }
                    if(cell.getColumnIndex() == 24){
                        purchaseInfoDto.setSpareSupplier3(cell.getRichStringCellValue().getString());
                    }
                    if(cell.getColumnIndex() == 25){
                        purchaseInfoDto.setSpareSupplier3BuyPrice(this.getBigDecimalFromString(cell.getRichStringCellValue().getString()));

                        List<JSONObject> list = new ArrayList<>();
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id", purchaseInfoDto.getSupplier());
                        jsonObject.put("text", purchaseInfoDto.getSaleUnitPrice() + "");
                        list.add(jsonObject);
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("id", purchaseInfoDto.getSpareSupplier1());
                        jsonObject1.put("text", purchaseInfoDto.getSpareSupplier1BuyPrice() + "");
                        list.add(jsonObject1);
                        JSONObject jsonObject2 = new JSONObject();
                        jsonObject2.put("id", purchaseInfoDto.getSpareSupplier2());
                        jsonObject2.put("text", purchaseInfoDto.getSpareSupplier2BuyPrice() + "");
                        list.add(jsonObject2);
                        JSONObject jsonObject3 = new JSONObject();
                        jsonObject3.put("id", purchaseInfoDto.getSpareSupplier3());
                        jsonObject3.put("text", purchaseInfoDto.getSpareSupplier3BuyPrice() + "");
                        list.add(jsonObject3);
                        purchaseInfoDto.setSupplierList(JSON.toJSONString(list));
                    }
                    if(cell.getColumnIndex() == 26){
                        purchaseInfoDto.setAS(cell.getRichStringCellValue().getString());
                    }
                    if(cell.getColumnIndex() == 27){
                        purchaseInfoDto.setWOODAUTO(cell.getRichStringCellValue().getString());
                    }

                    purchaseInfoDto.setPackType("zxbz");
//                    //金额=数量*采购价
//                    purchaseInfoDto.setMoney(new BigDecimal(purchaseInfoDto.getNum()).multiply(purchaseInfoDto.getBuyPrice()));
//                    //件数=数量/每件数量
//                    purchaseInfoDto.setPackNum((new BigDecimal(purchaseInfoDto.getNum()).divide(new BigDecimal(purchaseInfoDto.getEachPackNum()))).intValue());
//                    //总净重=数量*净重
//                    purchaseInfoDto.setSumNetWeight(new BigDecimal(purchaseInfoDto.getNum()).multiply(purchaseInfoDto.getNetWeight()));
//                    //总毛重=数量*毛重
//                    purchaseInfoDto.setSumGrossWeight(new BigDecimal(purchaseInfoDto.getNum()).multiply(purchaseInfoDto.getGrossWeight()));

                }
                if(rowIndex > 0){
                    purchaseInfoMapper.addPurchaseInfo(purchaseInfoDto);
                    if(!StringUtil.isNullString(path) && xSSFPictureData != null) {
                        ExcelUtil.savePicture(path, xSSFPictureData);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("导入时出错行数：" + (rowIndex + 1), e);
        }
    }


    private Integer getIntegerFromString(String value){
        return StringUtil.isNullString(value) ? Integer.valueOf(0) : Integer.valueOf(value);
    }

    private BigDecimal getBigDecimalFromString(String value){
        return StringUtil.isNullString(value) ? BigDecimal.ZERO : new BigDecimal(value);
    }
}