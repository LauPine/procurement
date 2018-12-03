package cn.com.liucm.service.impl;

import cn.com.liucm.constant.ControllerConstants;
import cn.com.liucm.dto.PageDto;
import cn.com.liucm.dto.PurchaseInfoDto;
import cn.com.liucm.entity.PurchaseInfoEntity;
import cn.com.liucm.mapper.BatchMapper;
import cn.com.liucm.mapper.PurchaseInfoMapper;
import cn.com.liucm.service.PurchaseInfoService;
import cn.com.liucm.util.ExcelUtil;
import cn.com.liucm.util.StringUtil;
import cn.com.liucm.util.UUIDGeneratorUtil;
import com.github.pagehelper.Page;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;

/**
 * 采购信息服务层实现类
 * Created by liucm on 2017-3-13.
 */
@Service
public class PurchaseInfoServiceImpl implements PurchaseInfoService {
    @Value("${file.upload.location}")
    private String fileUploadLocation;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private PurchaseInfoImporter purchaseInfoImporter;

    @Autowired
    private PurchaseInfoMapper purchaseInfoMapper;

    @Autowired
    BatchMapper batchMapper;

    @Override
    public void upload(final MultipartFile file) {
        DateTime now = new DateTime();
        String datePath = now.toString("/yyyy/MM/");
        File parent = new File(fileUploadLocation + datePath);
        if (!parent.exists()) {
            parent.mkdirs();
        }
        final String path = fileUploadLocation + datePath + UUIDGeneratorUtil.getUUID() + ".xlsx";
        //异步操作，先让http请求返回
        threadPoolTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                //purchaseInfoImporter.importFile(path);
                purchaseInfoImporter.importPictureFile(path, file);
            }
        });
    }

    @Override
    public PageDto<PurchaseInfoDto> queryPurchaseInfo(PurchaseInfoDto purchaseInfoDto) {
        PurchaseInfoEntity purchaseInfoEntity = this.createPurchaseInfoEntity(purchaseInfoDto);
        List<PurchaseInfoDto> searchList = purchaseInfoMapper.queryPurchaseInfo(purchaseInfoEntity);
        //分页并返回
        long count = ((Page<PurchaseInfoDto>) searchList).getTotal();
        PageDto<PurchaseInfoDto> pageDto = new PageDto(searchList, count, purchaseInfoEntity.getPageIndex(), purchaseInfoEntity.getPageSize());
        return pageDto;
    }

    @Override
    public List<PurchaseInfoDto> queryPurchaseInfoByResult(PurchaseInfoDto purchaseInfoDto) {
        List<PurchaseInfoDto> lastResultList = new LinkedList<PurchaseInfoDto>();
        if(!StringUtil.isNullString(purchaseInfoDto.getAS())) {
            String[] aSArray = purchaseInfoDto.getAS().split("\n");
            for (String temp1 : aSArray) {
                PurchaseInfoDto purchaseInfoDtoTemp = new PurchaseInfoDto();
                purchaseInfoDtoTemp.setPageSize(0);
                purchaseInfoDtoTemp.setCompanyNo("'" + purchaseInfoDto.getCompanyNo() + "'");
                purchaseInfoDtoTemp.setAS(temp1);
                PurchaseInfoEntity purchaseInfoEntity = this.createPurchaseInfoEntity(purchaseInfoDtoTemp);
                List<PurchaseInfoDto> searchList = purchaseInfoMapper.queryPurchaseInfo(purchaseInfoEntity);
                if(searchList.isEmpty()){
                    PurchaseInfoDto purchaseInfoDtoTemp1 = new PurchaseInfoDto();
                    purchaseInfoDtoTemp1.setSearchAS(temp1);
                    lastResultList.add(purchaseInfoDtoTemp1);
                } else {
                    for(PurchaseInfoDto purchaseInfoDtoTemp2 : searchList){
                        String asStringTempAll = StringUtil.isNullString(purchaseInfoDtoTemp2.getAS()) ? "" : purchaseInfoDtoTemp2.getAS();
                        String woodAutoStringTempAll = StringUtil.isNullString(purchaseInfoDtoTemp2.getWOODAUTO()) ? "" : purchaseInfoDtoTemp2.getWOODAUTO();
                        String[] asStringTempArray = asStringTempAll.split("\n");
                        String[] woodAutoStringTempArray = woodAutoStringTempAll.split("\n");
                        List<String> matchAsListResult = new LinkedList<>();
                        List<String> matchWoodAutoListResult = new LinkedList<>();
                        for(String asStringTemp: asStringTempArray){
                            String noSpecialString = asStringTemp.replaceAll("-","").replaceAll(",","").replaceAll("\\.","").replaceAll(" ","");
                            if(noSpecialString.indexOf(temp1) != -1){
                                matchAsListResult.add(asStringTemp);
                            }
                        }
                        for(String woodAutoStringTemp: woodAutoStringTempArray){
                            String noSpecialString = woodAutoStringTemp.replaceAll("-","").replaceAll(",","").replaceAll("\\.","").replaceAll(" ","");
                            if(noSpecialString.indexOf(temp1) != -1){
                                matchWoodAutoListResult.add(woodAutoStringTemp);
                            }
                        }
                        String matchAS = "";
                        String matchWOODAUTO = "";
                        for(int i = 0; i < matchAsListResult.size(); i++){
                            matchAS = matchAS + matchAsListResult.get(i);
                            if(i < matchAsListResult.size() - 1){
                                matchAS = matchAS + "\n";
                            }
                        }
                        for(int i = 0; i < matchWoodAutoListResult.size(); i++){
                            matchWOODAUTO = matchWOODAUTO + matchWoodAutoListResult.get(i);
                            if(i < matchWoodAutoListResult.size() - 1){
                                matchWOODAUTO = matchWOODAUTO + "\n";
                            }
                        }
                        purchaseInfoDtoTemp2.setSearchAS(temp1);
                        purchaseInfoDtoTemp2.setMatchAS(matchAS);
                        purchaseInfoDtoTemp2.setMatchWOODAUTO(matchWOODAUTO);
                    }
                    lastResultList.addAll(searchList);
                }
            }
        }
        return lastResultList;
    }

    @Override
    public void updatePurchaseInfo(List<PurchaseInfoDto> purchaseInfoDtoList) {
        for(PurchaseInfoDto purchaseInfoDto : purchaseInfoDtoList) {
            if(purchaseInfoDto.getNum() != null) {
                //采购总价=数量*采购单价
                purchaseInfoDto.setBuyTotalPrice(new BigDecimal(purchaseInfoDto.getNum()).multiply(purchaseInfoDto.getBuyUnitPrice()));
                //销售总价=数量*销售单价
                purchaseInfoDto.setSaleTotalPrice(new BigDecimal(purchaseInfoDto.getNum()).multiply(purchaseInfoDto.getSaleUnitPrice()));
                //利率=销售单价/采购单价
                purchaseInfoDto.setRate(purchaseInfoDto.getSaleUnitPrice().divide(purchaseInfoDto.getBuyUnitPrice(), 2, BigDecimal.ROUND_DOWN));
                //件数=数量/每件数量
                purchaseInfoDto.setPackNum((new BigDecimal(purchaseInfoDto.getNum()).divide(new BigDecimal(purchaseInfoDto.getEachPackNum()),2, BigDecimal.ROUND_DOWN)).intValue());
                //总净重=数量*净重
                purchaseInfoDto.setSumNetWeight(new BigDecimal(purchaseInfoDto.getNum()).multiply(purchaseInfoDto.getNetWeight()));
                //总毛重=数量*毛重
                purchaseInfoDto.setSumGrossWeight(new BigDecimal(purchaseInfoDto.getNum()).multiply(purchaseInfoDto.getGrossWeight()));
            }
            if(purchaseInfoDto.getSize() != null) {
                //体积m3
                String[] sizeArray = purchaseInfoDto.getSize().split("\\*");
                BigDecimal volume = BigDecimal.ONE;
                for (String sizeTemp : sizeArray) {
                    volume = volume.multiply(new BigDecimal(sizeTemp));
                }
                purchaseInfoDto.setVolume(volume.divide(new BigDecimal(1000000)));
            }
        }
        batchMapper.batchInsert(PurchaseInfoMapper.class, "updatePurchaseInfo", purchaseInfoDtoList);
        //purchaseInfoMapper.updatePurchaseInfo(purchaseInfoDto);
    }

    @Override
    public void exportPurchaseInfo(PurchaseInfoDto purchaseInfoDto, HttpServletResponse response) {
        //根据条件查询解答数统计信息
        PurchaseInfoEntity purchaseInfoEntity = this.createPurchaseInfoEntity(purchaseInfoDto);
        purchaseInfoEntity.setPageSize(0);
        List<PurchaseInfoDto> searchList = purchaseInfoMapper.queryPurchaseInfo(purchaseInfoEntity);
        //根据不同sheet循环创建Map，现在只有一个sheet
        List<List<Map<String, Object>>> dataAllList = new ArrayList<List<Map<String, Object>>>();
        List<String[]> keysAllList = new ArrayList<String[]>();
        List<String[]> namesAllList = new ArrayList<String[]>();
        List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("sheetName", "采购系统导出sheet");
        listmap.add(map);
        for(PurchaseInfoDto purchaseInfoDtoTemp : searchList){
            Map<String, Object> mapValue = new HashMap<String, Object>();
            mapValue.put("companyNo", purchaseInfoDtoTemp.getCompanyNo());
            mapValue.put("picturePath", purchaseInfoDtoTemp.getPicturePath());
            mapValue.put("param", purchaseInfoDtoTemp.getParam());
            mapValue.put("showOEM", purchaseInfoDtoTemp.getShowOEM());
            mapValue.put("num", purchaseInfoDtoTemp.getNum());
            mapValue.put("buyUnitPrice", purchaseInfoDtoTemp.getBuyUnitPrice());
            mapValue.put("buyTotalPrice", purchaseInfoDtoTemp.getBuyTotalPrice());
            mapValue.put("supplier", purchaseInfoDtoTemp.getSupplier());
            mapValue.put("saleUnitPrice", purchaseInfoDtoTemp.getSaleUnitPrice());
            mapValue.put("saleTotalPrice", purchaseInfoDtoTemp.getSaleTotalPrice());
            mapValue.put("rate", purchaseInfoDtoTemp.getRate());
            String packTypeName = "中性包装";
            if("rdch".equals(purchaseInfoDtoTemp.getPackType())){
                packTypeName = "若迪彩盒";
            } else if("jlch".equals(purchaseInfoDtoTemp.getPackType())){
                packTypeName = "嘉禄彩盒";
            } else if("zwfch".equals(purchaseInfoDtoTemp.getPackType())){
                packTypeName = "ZWF彩盒";
            }
            mapValue.put("packType", packTypeName);
            mapValue.put("eachPackNum", purchaseInfoDtoTemp.getEachPackNum());
            mapValue.put("packNum", purchaseInfoDtoTemp.getPackNum());
            mapValue.put("sumNetWeight", purchaseInfoDtoTemp.getSumNetWeight());
            mapValue.put("sumGrossWeight", purchaseInfoDtoTemp.getSumGrossWeight());
            mapValue.put("volume", purchaseInfoDtoTemp.getVolume());
            mapValue.put("size", purchaseInfoDtoTemp.getSize());
            mapValue.put("netWeight", purchaseInfoDtoTemp.getNetWeight());
            mapValue.put("grossWeight", purchaseInfoDtoTemp.getGrossWeight());
            listmap.add(mapValue);
        }
        dataAllList.add(listmap);
        keysAllList.add(ControllerConstants.purchaseInfoColumnKeysQ);
        namesAllList.add(ControllerConstants.purchaseInfoColumnNamesQ);
        ExcelUtil.exportAllInfo(response, dataAllList, keysAllList, namesAllList,"采购信息导出文件" + new Date().getTime());
    }

    @Override
    public void exportPurchaseInfoByResult(PurchaseInfoDto purchaseInfoDto, HttpServletResponse response) {
        //根据条件查询解答数统计信息
        List<PurchaseInfoDto> searchList = this.queryPurchaseInfoByResult(purchaseInfoDto);
        //根据不同sheet循环创建Map，现在只有一个sheet
        List<List<Map<String, Object>>> dataAllList = new ArrayList<List<Map<String, Object>>>();
        List<String[]> keysAllList = new ArrayList<String[]>();
        List<String[]> namesAllList = new ArrayList<String[]>();
        List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("sheetName", "采购系统导出sheet");
        listmap.add(map);
        for(PurchaseInfoDto purchaseInfoDtoTemp : searchList){
            Map<String, Object> mapValue = new HashMap<String, Object>();
            mapValue.put("searchAS", purchaseInfoDtoTemp.getSearchAS());
            mapValue.put("matchAS", purchaseInfoDtoTemp.getMatchAS());
            mapValue.put("matchWOODAUTO", purchaseInfoDtoTemp.getMatchWOODAUTO());
            mapValue.put("companyNo", purchaseInfoDtoTemp.getCompanyNo());
            mapValue.put("picturePath", purchaseInfoDtoTemp.getPicturePath());
            mapValue.put("param", purchaseInfoDtoTemp.getParam());
            mapValue.put("showOEM", purchaseInfoDtoTemp.getShowOEM());
            mapValue.put("num", purchaseInfoDtoTemp.getNum());
            mapValue.put("buyUnitPrice", purchaseInfoDtoTemp.getBuyUnitPrice());
            mapValue.put("buyTotalPrice", purchaseInfoDtoTemp.getBuyTotalPrice());
            mapValue.put("supplier", purchaseInfoDtoTemp.getSupplier());
            mapValue.put("saleUnitPrice", purchaseInfoDtoTemp.getSaleUnitPrice());
            mapValue.put("saleTotalPrice", purchaseInfoDtoTemp.getSaleTotalPrice());
            mapValue.put("rate", purchaseInfoDtoTemp.getRate());
            String packTypeName = "中性包装";
            if("rdch".equals(purchaseInfoDtoTemp.getPackType())){
                packTypeName = "若迪彩盒";
            } else if("jlch".equals(purchaseInfoDtoTemp.getPackType())){
                packTypeName = "嘉禄彩盒";
            } else if("zwfch".equals(purchaseInfoDtoTemp.getPackType())){
                packTypeName = "ZWF彩盒";
            }
            mapValue.put("packType", packTypeName);
            mapValue.put("eachPackNum", purchaseInfoDtoTemp.getEachPackNum());
            mapValue.put("packNum", purchaseInfoDtoTemp.getPackNum());
            mapValue.put("sumNetWeight", purchaseInfoDtoTemp.getSumNetWeight());
            mapValue.put("sumGrossWeight", purchaseInfoDtoTemp.getSumGrossWeight());
            mapValue.put("volume", purchaseInfoDtoTemp.getVolume());
            mapValue.put("size", purchaseInfoDtoTemp.getSize());
            mapValue.put("netWeight", purchaseInfoDtoTemp.getNetWeight());
            mapValue.put("grossWeight", purchaseInfoDtoTemp.getGrossWeight());
            listmap.add(mapValue);
        }
        dataAllList.add(listmap);
        keysAllList.add(ControllerConstants.purchaseInfoColumnKeysQ);
        namesAllList.add(ControllerConstants.purchaseInfoColumnNamesQ);
        ExcelUtil.exportAllInfo(response, dataAllList, keysAllList, namesAllList,"采购信息导出文件" + new Date().getTime());
    }

    @Override
    public void deletePurchaseInfo(List<String> companyNoList) {
        batchMapper.batchDelete(PurchaseInfoMapper.class, "deletePurchaseInfo", companyNoList);
    }

    @Override
    public String getNoResultAS(PurchaseInfoDto purchaseInfoDto) {
        PurchaseInfoEntity purchaseInfoEntity = this.createPurchaseInfoEntity(purchaseInfoDto);
        purchaseInfoEntity.setPageSize(0);
        List<PurchaseInfoDto> searchList = purchaseInfoMapper.queryPurchaseInfo(purchaseInfoEntity);
        String noSpecialStringAS = purchaseInfoDto.getAS().replaceAll("-","").replaceAll(",","").replaceAll("\\.","").replaceAll(" ","");
        List<String> searchListResult = new ArrayList<String>();
        String noResultString = "";
        if(!StringUtil.isNullString(noSpecialStringAS)) {
            String[] noSpecialStringASArray = noSpecialStringAS.split("\n");
            for (String temp1 : noSpecialStringASArray) {
                for(PurchaseInfoDto purchaseInfoDtoTemp : searchList){
                    String asStringTempAll = "";
                    String woodAutoStringTempAll = "";
                    if(!StringUtil.isNullString(purchaseInfoDtoTemp.getAS())){
                        asStringTempAll = purchaseInfoDtoTemp.getAS().replaceAll("-","").replaceAll(",","").replaceAll("\\.","").replaceAll(" ","");
                    }
                    if(!StringUtil.isNullString(purchaseInfoDtoTemp.getWOODAUTO())){
                        woodAutoStringTempAll = purchaseInfoDtoTemp.getWOODAUTO().replaceAll("-","").replaceAll(",","").replaceAll("\\.","").replaceAll(" ","");
                    }
                    String[] asStringTempArray = asStringTempAll.split("\n");
                    String[] woodAutoStringTempArray = woodAutoStringTempAll.split("\n");
                    boolean isEqual = false;
                    for(String asStringTemp: asStringTempArray){
                        if(asStringTemp.indexOf(temp1) != -1){
                            searchListResult.add(temp1);
                            isEqual = true;
                            break;
                        }
                    }
                    if(isEqual){
                        break;
                    }
                    for(String woodAutoStringTemp: woodAutoStringTempArray){
                        if(woodAutoStringTemp.indexOf(temp1) != -1){
                            searchListResult.add(temp1);
                            isEqual = true;
                            break;
                        }
                    }
                    if(isEqual){
                        break;
                    }
                }
            }
            for(String temp1 : noSpecialStringASArray) {
                if(!searchListResult.contains(temp1)){
                    noResultString = noResultString + "<br>";
                    noResultString = noResultString + temp1;
                }
            }
        }
        return noResultString;
    }

    private PurchaseInfoEntity createPurchaseInfoEntity(PurchaseInfoDto purchaseInfoDto){
        String asQueryCondition = "";
        PurchaseInfoEntity purchaseInfoEntity = new PurchaseInfoEntity();
        BeanUtils.copyProperties(purchaseInfoDto, purchaseInfoEntity);
        String noSpecialStringAS = purchaseInfoEntity.getAS().replaceAll("-","").replaceAll(",","").replaceAll("\\.","").replaceAll(" ","");
        if(!StringUtil.isNullString(noSpecialStringAS)) {
            String[] noSpecialStringASArray = noSpecialStringAS.split("\n");
            for (int i = 0; i < noSpecialStringASArray.length; i++) {
                String temp = noSpecialStringASArray[i];
                if(i == 0){
                    asQueryCondition = "(" + getAsQueryCondition(temp) + ")";
                } else {
                    asQueryCondition = "(" + asQueryCondition + " or (" + getAsQueryCondition(temp) + "))";
                }
            }
            purchaseInfoEntity.setAS(asQueryCondition);
        }
        return purchaseInfoEntity;
    }

    private String getAsQueryCondition(String temp){
        return "replace(replace(replace(replace(`AS`, '-', ''), ',', ''), '.', ''), ' ','') like concat('%', '" + temp + "', '%') or replace(replace(replace(replace(`WOODAUTO`, '-', ''), ',', ''), '.', ''), ' ','') like concat('%', '" + temp +"', '%')";
    }

    public static void main(String[] args){
        String a = " asdasd-hh,as d. ";
        System.out.println(a.replaceAll("-","").replaceAll(",","").replaceAll("\\.","").replaceAll(" ",""));
    }

}
