package cn.com.liucm.entity;

import java.math.BigDecimal;

public class PurchaseInfoEntity extends RequestPageEntity{
    /**
     * 公司编号
     */
    private String companyNo;
    /**
     * 产品图片路径
     */
    private String picturePath;
    /**
     * 产品参数
     */
    private String param;
    /**
     * 显示的OEM
     */
    private String showOEM;
    /**
     * 数量
     */
    private Integer num;
    /**
     * 采购单价
     */
    private BigDecimal buyUnitPrice;
    /**
     * 采购总价
     */
    private BigDecimal buyTotalPrice;
    /**
     * 供应商
     */
    private String supplier;

    /**
     * 销售单价
     */
    private BigDecimal saleUnitPrice;

    /**
     * 销售总价
     */
    private BigDecimal saleTotalPrice;
    /**
     * 利率
     */
    private BigDecimal rate;
    /**
     * 包装方式
     */
    private String packType;
    /**
     * 每件数量
     */
    private Integer eachPackNum;
    /**
     * 件数
     */
    private Integer packNum;
    /**
     * 总净重
     */
    private BigDecimal sumNetWeight;
    /**
     * 总毛重
     */
    private BigDecimal sumGrossWeight;
    /**
     * 体积
     */
    private BigDecimal volume;
    /**
     * 尺寸
     */
    private String size;
    /**
     * 净重
     */
    private BigDecimal netWeight;
    /**
     * 毛重
     */
    private BigDecimal grossWeight;
    /**
     * 备用供应商1
     */
    private String spareSupplier1;
    /**
     * 备用供应商1采购价
     */
    private BigDecimal spareSupplier1BuyPrice;
    /**
     * 备用供应商2
     */
    private String spareSupplier2;
    /**
     * 备用供应商2采购价
     */
    private BigDecimal spareSupplier2BuyPrice;
    /**
     * 备用供应商3
     */
    private String spareSupplier3;
    /**
     * 备用供应商3采购价
     */
    private BigDecimal spareSupplier3BuyPrice;
    /**
     * AS
     */
    private String AS;
    /**
     * WOODAUTO
     */
    private String WOODAUTO;

    /**
     * 供应商价格列表
     */
    private String supplierList;

    public String getCompanyNo() {
        return companyNo;
    }

    public void setCompanyNo(String companyNo) {
        this.companyNo = companyNo;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getShowOEM() {
        return showOEM;
    }

    public void setShowOEM(String showOEM) {
        this.showOEM = showOEM;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public BigDecimal getBuyUnitPrice() {
        return buyUnitPrice;
    }

    public void setBuyUnitPrice(BigDecimal buyUnitPrice) {
        this.buyUnitPrice = buyUnitPrice;
    }

    public BigDecimal getBuyTotalPrice() {
        return buyTotalPrice;
    }

    public void setBuyTotalPrice(BigDecimal buyTotalPrice) {
        this.buyTotalPrice = buyTotalPrice;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public BigDecimal getSaleUnitPrice() {
        return saleUnitPrice;
    }

    public void setSaleUnitPrice(BigDecimal saleUnitPrice) {
        this.saleUnitPrice = saleUnitPrice;
    }

    public BigDecimal getSaleTotalPrice() {
        return saleTotalPrice;
    }

    public void setSaleTotalPrice(BigDecimal saleTotalPrice) {
        this.saleTotalPrice = saleTotalPrice;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public String getPackType() {
        return packType;
    }

    public void setPackType(String packType) {
        this.packType = packType;
    }

    public Integer getEachPackNum() {
        return eachPackNum;
    }

    public void setEachPackNum(Integer eachPackNum) {
        this.eachPackNum = eachPackNum;
    }

    public Integer getPackNum() {
        return packNum;
    }

    public void setPackNum(Integer packNum) {
        this.packNum = packNum;
    }

    public BigDecimal getSumNetWeight() {
        return sumNetWeight;
    }

    public void setSumNetWeight(BigDecimal sumNetWeight) {
        this.sumNetWeight = sumNetWeight;
    }

    public BigDecimal getSumGrossWeight() {
        return sumGrossWeight;
    }

    public void setSumGrossWeight(BigDecimal sumGrossWeight) {
        this.sumGrossWeight = sumGrossWeight;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public BigDecimal getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(BigDecimal netWeight) {
        this.netWeight = netWeight;
    }

    public BigDecimal getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(BigDecimal grossWeight) {
        this.grossWeight = grossWeight;
    }

    public String getSpareSupplier1() {
        return spareSupplier1;
    }

    public void setSpareSupplier1(String spareSupplier1) {
        this.spareSupplier1 = spareSupplier1;
    }

    public BigDecimal getSpareSupplier1BuyPrice() {
        return spareSupplier1BuyPrice;
    }

    public void setSpareSupplier1BuyPrice(BigDecimal spareSupplier1BuyPrice) {
        this.spareSupplier1BuyPrice = spareSupplier1BuyPrice;
    }

    public String getSpareSupplier2() {
        return spareSupplier2;
    }

    public void setSpareSupplier2(String spareSupplier2) {
        this.spareSupplier2 = spareSupplier2;
    }

    public BigDecimal getSpareSupplier2BuyPrice() {
        return spareSupplier2BuyPrice;
    }

    public void setSpareSupplier2BuyPrice(BigDecimal spareSupplier2BuyPrice) {
        this.spareSupplier2BuyPrice = spareSupplier2BuyPrice;
    }

    public String getSpareSupplier3() {
        return spareSupplier3;
    }

    public void setSpareSupplier3(String spareSupplier3) {
        this.spareSupplier3 = spareSupplier3;
    }

    public BigDecimal getSpareSupplier3BuyPrice() {
        return spareSupplier3BuyPrice;
    }

    public void setSpareSupplier3BuyPrice(BigDecimal spareSupplier3BuyPrice) {
        this.spareSupplier3BuyPrice = spareSupplier3BuyPrice;
    }

    public String getAS() {
        return AS;
    }

    public void setAS(String AS) {
        this.AS = AS;
    }

    public String getWOODAUTO() {
        return WOODAUTO;
    }

    public void setWOODAUTO(String WOODAUTO) {
        this.WOODAUTO = WOODAUTO;
    }

    public String getSupplierList() {
        return supplierList;
    }

    public void setSupplierList(String supplierList) {
        this.supplierList = supplierList;
    }
}
