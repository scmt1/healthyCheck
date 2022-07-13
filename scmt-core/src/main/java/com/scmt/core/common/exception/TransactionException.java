package com.scmt.core.common.exception;

import lombok.Data;

/**
 * @author Exrickx
 */
@Data
public class TransactionException extends Exception {

    private String msg;

    public TransactionException(String msg) {
        super(msg);
        this.msg = msg;
    }
}
