package cn.com.liucm.util;

import java.util.UUID;

/**
 * Created by liucm on 2017-3-21.
 */
public class UUIDGeneratorUtil {
    /**
     * Instantiates a new UUID generator.
     */
    private UUIDGeneratorUtil() {
    }

    /**
     * 获得一个UUID.
     *
     * @return String UUID
     */
    public static String getUUID() {
        String s = UUID.randomUUID().toString();
        // 去掉“-”符号
        return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23) + s.substring(24);
    }
}
