package cn.com.liucm.exception.handler;

import cn.com.liucm.dto.ResultDto;
import cn.com.liucm.exception.ProcurementDataException;
import cn.com.liucm.exception.ProcurementServiceException;
import cn.com.liucm.exception.ProcurementValidatorException;
import cn.com.liucm.support.MessageSupport;
import cn.com.liucm.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 请求控制器访问异常控制处理类，全局统一处理Controller入口产生的异常
 *
 *
 * Created by wangql on 2017/3/14.
 */
@ControllerAdvice
public class ControllerExceptionHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @Autowired
    private MessageSupport messageSupport;

    @ExceptionHandler(ProcurementServiceException.class)
    @ResponseBody
    public ResultDto handleTelSaleServiceException(Exception e) {
        LOGGER.error(e.getMessage(), e);
        ResultDto resultDto = new ResultDto();
        resultDto.setSuccess(false);
        resultDto.setMessageCode(e.getMessage());
        resultDto.setMessage(messageSupport.getMessage(e.getMessage()));
        return resultDto;
    }

    @ExceptionHandler(ProcurementDataException.class)
    @ResponseBody
    public ResultDto handleTelSaleDataException(Exception e) {
        ResultDto resultDto = new ResultDto();
        resultDto.setSuccess(false);
        resultDto.setMessageCode(e.getMessage());
        resultDto.setMessage(messageSupport.getMessage(e.getMessage()));
        return resultDto;
    }

    @ExceptionHandler(ProcurementValidatorException.class)
    @ResponseBody
    public ResultDto handleTelSaleValidatorException(Exception e) {
        ResultDto resultDto = new ResultDto();
        resultDto.setSuccess(false);
        resultDto.setMessageCode("api.data.verification.error");
        resultDto.setMessage(e.getMessage());
        return resultDto;
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResultDto handleOtherException(Exception e) {
        if (StringUtils.containsIgnoreCase(ExceptionUtils.getRootCauseMessage(e), "Broken pipe")) {
            LOGGER.warn(e.getMessage());
            return null;
        }else{
            LOGGER.error(e.getMessage(), e);
            ResultDto resultDto = new ResultDto();
            resultDto.setSuccess(false);
            if (!StringUtil.isNullString(e.getMessage())
                    && (e.getMessage().contains("ORA") || e.getMessage().contains("sql"))) {
                resultDto.setMessageCode("api.database_error");
            } else {
                resultDto.setMessageCode("api.unpredictable_exception");
            }
            resultDto.setMessage(messageSupport.getMessage(resultDto.getMessageCode()));
            return resultDto;
        }
    }
}
