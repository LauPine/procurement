package cn.com.liucm.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    private StringUtil(){
    }

    /**
     * 判断字符串是否为null或空
     * @param str
     * @return
     */
    public static boolean isNullString(String str) {
	    return str == null || "".equals(str);
    }

    /** oracle特殊字符--转义符. */
    public static final String SQL_SYMBOL = "|";

    /**
     * 对oracle模糊查询进行特殊字符转义处理 转义符:|、%、_、'.
     *
     * @param sql
     *            SQL语句
     * @return 转义处理后的SQL脚本
     */
    public static String sqlEncode(String sql) {
        if(StringUtil.isNullString(sql)){
            return "";
        }
        return sql.replace(SQL_SYMBOL, SQL_SYMBOL + SQL_SYMBOL).replace("%", SQL_SYMBOL + "%").replace("_",
                SQL_SYMBOL + "_").replace("'", "''").replace("％", SQL_SYMBOL + "％").replace("＿", SQL_SYMBOL + "＿");
    }

    /**
     * 正则表达式判断是否数字(正负包括.)
     * @param str
     * @return
     */
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("^-?\\d+$");
        Matcher isNum = pattern.matcher(str);
        if(!isNum.matches()){
            return false;
        }
        return true;
    }

    /**
     * 正则表达式判断是正整数+0
     * @param str
     * @return
     */
    public static boolean isPositiveIntegerOrZero(String str){
        Pattern pattern = Pattern.compile("^\\d+$");
        Matcher isNum = pattern.matcher(str);
        if(!isNum.matches()){
            return false;
        }
        return true;
    }

    /**
     * 正则表达式判断是否是邮箱
     * @param str
     * @return
     */
    public static boolean isEmail(String str){
        Pattern pattern = Pattern.compile("^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$");
        Matcher isNum = pattern.matcher(str);
        if(!isNum.matches()){
            return false;
        }
        return true;
    }

    /**
     * 判断只能输入数字和-
     * @param str
     * @return
     */
    public static boolean isPhoneNumber(String str){
        Pattern pattern = Pattern.compile("^[-0-9]+$");
        Matcher isNum = pattern.matcher(str);
        if(!isNum.matches()){
            return false;
        }
        return true;
    }

    /**
     * 判断是是否是手机号码
     * @param mobile
     * @return
     */
    public static boolean checkMobile(String mobile) {
        if (!isNullString(mobile)) {
            Pattern keywordPattern = Pattern.compile("^1\\d{10}$");
            Matcher matcher = keywordPattern.matcher(mobile);
            if (!matcher.find()) {
                return false;// 手机由11位数字组成，格式为：1xxxxxxxxxx
            }
        } else {
            return false;//手机号码不能为空
        }
        return true;
    }
}
