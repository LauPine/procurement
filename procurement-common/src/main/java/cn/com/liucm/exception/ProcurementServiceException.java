package cn.com.liucm.exception;

import java.io.Serializable;

/**
 * Created by liucm on 2017-3-22.
 */
public class ProcurementServiceException extends RuntimeException implements Serializable {

    public ProcurementServiceException() {
        super();
    }

    public ProcurementServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcurementServiceException(String message) {
        super(message);
    }

    public ProcurementServiceException(Throwable cause) {
        super(cause);
    }

}
