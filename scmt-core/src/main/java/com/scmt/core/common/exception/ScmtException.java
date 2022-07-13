package com.scmt.core.common.exception;

import lombok.Data;

/**
 * @author Exrickx
 */
@Data
public class ScmtException extends RuntimeException {

    private String msg;

    public ScmtException(String msg) {
        super(msg);
        this.msg = msg;
    }
}
