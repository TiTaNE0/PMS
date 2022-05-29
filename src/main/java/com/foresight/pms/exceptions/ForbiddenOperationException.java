package com.foresight.pms.exceptions;

public class ForbiddenOperationException extends RuntimeException {
    public ForbiddenOperationException(String s) {
        super(s);
    }
}
