package cn.com.liucm.mapper;

import cn.com.liucm.dto.PurchaseInfoDto;
import cn.com.liucm.entity.PurchaseInfoEntity;

import java.util.List;

/**
 * 采购系统映射
 *
 * @author liucm
 * @created 2017-06-20
 */
public interface PurchaseInfoMapper {
    /**
     * 采购信息
     * @param purchaseInfoDto 采购信息对象
     */
    void addPurchaseInfo(PurchaseInfoDto purchaseInfoDto) throws Exception;

    List<PurchaseInfoDto> queryPurchaseInfo(PurchaseInfoEntity purchaseInfoEntity);

    void updatePurchaseInfo(PurchaseInfoDto purchaseInfoDto) throws Exception;

    void deletePurchaseInfo(String companyNo) throws Exception;

    int countPurchaseInfo(String companyNo);
}
