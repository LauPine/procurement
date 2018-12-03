package cn.com.liucm.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * <p> Function: [功能：message信息支持类] </p>
 * Created by liucm on 2017-3-10.
 */
@Component
public class MessageSupport {
    @Autowired
    private ResourceBundleMessageSource messageSource;

    /**
     *
     * @param code
     *            code
     * @return msg
     */
    public String getMessage(String code) {
        return getMessage(code, null);
    }

    /**
     *
     * @param code
     *            code
     * @param args
     *            参数
     * @return msg
     */
    public String getMessage(String code, String[] args) {
        return messageSource.getMessage(code, args, Locale.CHINA);
    }

}
