package cn.com.liucm.dto;

import java.io.Serializable;

/**
 * 统一前后端交互实体
 * Created by liucm on 2017-3-10.
 */
public class ResultDto<T> implements Serializable {
    private boolean success;
    private String message;
    private String messageCode;
    private T data;

    public ResultDto() {

    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
