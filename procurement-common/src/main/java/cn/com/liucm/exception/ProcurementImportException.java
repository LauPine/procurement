package cn.com.liucm.exception;

import java.io.Serializable;

/**
 * Created by liucm on 2017-3-21.
 */
public class ProcurementImportException extends RuntimeException implements Serializable{
    public ProcurementImportException(String message) {
        super(message);
    }
}
