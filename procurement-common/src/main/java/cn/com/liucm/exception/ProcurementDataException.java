package cn.com.liucm.exception;

import java.io.Serializable;

/**
 * <p> Project: tel-sale </p>
 * <p> Function: [功能：电销-我的任务实现类] </p>
 * <p> Description: [功能描述：电销-我的任务] </p>
 * <p> Copyright: Copyright(c) 2009-2018 税友集团 </p>
 * <p> Company: 税友集团</p>
 * <p> UpdateDate:2017-03-13</p>
 * <p> Updator:zhousj</p>
 *
 * @version 5.0
 * @since 5.0
 */
public class ProcurementDataException extends RuntimeException implements Serializable {

    public ProcurementDataException() {
        super();
    }

    public ProcurementDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcurementDataException(String message) {
        super(message);
    }

    public ProcurementDataException(Throwable cause) {
        super(cause);
    }

}
