package cn.com.liucm.exception;

import java.io.Serializable;

/**
 * <p> Project: tel-sale </p>
 * <p> Function: [功能：数据有效性验证异常] </p>
 * <p> Description: [功能描述：] </p>
 * <p> Copyright: Copyright(c) 2009-2018 税友集团 </p>
 * <p> Company: 税友集团</p>
 * <p> UpdateDate:2017-03-16</p>
 * <p> Updator:pyl</p>
 *
 * @version 5.0
 * @since 5.0
 */
public class ProcurementValidatorException extends RuntimeException implements Serializable {
    public ProcurementValidatorException() {
        super();
    }

    public ProcurementValidatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcurementValidatorException(String message) {
        super(message);
    }

    public ProcurementValidatorException(Throwable cause) {
        super(cause);
    }
}
