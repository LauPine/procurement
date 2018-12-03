package cn.com.liucm.service;

import cn.com.liucm.dto.PageDto;
import cn.com.liucm.dto.PurchaseInfoDto;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 采购信息服务层
 *
 * Created by liucm on 2017-3-13.
 */
public interface PurchaseInfoService {

    void upload(MultipartFile file);

    PageDto<PurchaseInfoDto> queryPurchaseInfo(PurchaseInfoDto purchaseInfoDto);

    List<PurchaseInfoDto> queryPurchaseInfoByResult(PurchaseInfoDto purchaseInfoDto);

    void updatePurchaseInfo(List<PurchaseInfoDto> purchaseInfoDtoList);

    void exportPurchaseInfo(PurchaseInfoDto purchaseInfoDto, HttpServletResponse response);

    void exportPurchaseInfoByResult(PurchaseInfoDto purchaseInfoDto, HttpServletResponse response);

    void deletePurchaseInfo(List<String> companyNoList);

    String getNoResultAS(PurchaseInfoDto purchaseInfoDto);

}
