package cn.com.liucm.controller;

import cn.com.liucm.constant.ControllerConstants;
import cn.com.liucm.dto.ResultDto;
import cn.com.liucm.support.MessageSupport;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpSession;

/**
 * Description: [功能描述：报错信息、返回值处理]
 * Created by liucm on 2017-3-10.
 */
public class BaseController {

    @Autowired
    private MessageSupport messageSupport;

    @Autowired
    private HttpSession httpSession;

    public ResultDto ajaxDoneFail(Exception ex) {
        return ajaxDoneFail(ControllerConstants.STATUS_CODE_500, ex.getMessage());
    }

    public ResultDto ajaxDoneFail(String message, Exception ex) {
        return ajaxDoneFail(ControllerConstants.STATUS_CODE_500, message + ex.getMessage());
    }

    public ResultDto ajaxDoneFail(String msgCode) {
        return ajaxDoneFail(msgCode, messageSupport.getMessage(msgCode));
    }

    private ResultDto ajaxDoneFail(String msgCode, String message) {
        return ajaxDone(false, msgCode, message, "");
    }

    public ResultDto ajaxDoneSuccess() {
        return ajaxDoneSuccess("");
    }

    public ResultDto ajaxDoneSuccess(Object data) {
        return ajaxDoneSuccess(data, ControllerConstants.STATUS_CODE_200);
    }

    public ResultDto ajaxDoneSuccess(Object data, String messageCode) {
        return ajaxDoneSuccess(data, messageCode, messageSupport.getMessage(messageCode));
    }

    public ResultDto ajaxDoneSuccess(Object data, String msgCode, String message) {
        return ajaxDone(true, msgCode, message, data);
    }

    private ResultDto ajaxDone(boolean success, String msgCode, String message, Object data) {
        ResultDto messageDto = new ResultDto();
        messageDto.setSuccess(success);
        messageDto.setMessageCode(msgCode);
        messageDto.setMessage(message);
        messageDto.setData(data);
        return messageDto;
    }

}
