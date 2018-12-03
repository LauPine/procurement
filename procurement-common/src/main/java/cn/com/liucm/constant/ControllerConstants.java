package cn.com.liucm.constant;

/**
 * Title: 控制层常量
 * Created by liucm on 2017-3-10.
 */
public class ControllerConstants {

    /**
     * 成功
     */
    public static final String STATUS_CODE_00000000 = "00000000";
    /**
     * 正常
     */
    public static final String STATUS_CODE_200 = "success";
    /**
     * 异常--系统错误
     */
    public static final String STATUS_CODE_500 = "error";

    /**
     * 列名对应的键
     */
    public static final String purchaseInfoColumnKeysQ[] = {"searchAS", "matchAS", "matchWOODAUTO", "companyNo", "picturePath", "param", "showOEM", "num", "buyUnitPrice", "buyTotalPrice", "supplier"
            , "saleUnitPrice", "saleTotalPrice", "rate", "packType", "eachPackNum", "packNum", "sumNetWeight", "sumGrossWeight", "volume", "size", "netWeight",
            "grossWeight"};

    /**
     * 对应列名
     */
    public static final String[] purchaseInfoColumnNamesQ ={"查询AS/WOODAUTO", "匹配的AS", "匹配的WOODAUTO", "公司编号", "产品图片", "产品参数", "显示的OEM", "数量", "采购单价", "采购总价"
            ,"供应商", "销售单价", "销售总价", "利率", "包装方式", "每件数量", "件数"
            ,"总净重", "总毛重", "体积", "尺寸", "净重", "毛重"};

    private ControllerConstants(){
    }
}
