package com.dcits.im.jpa.repository.exception;


public class MoreThanOneDataFoundException extends RuntimeException {

    public MoreThanOneDataFoundException() {
    }

    public MoreThanOneDataFoundException(String message) {
        super(message);
    }

    public MoreThanOneDataFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MoreThanOneDataFoundException(Throwable cause) {
        super(cause);
    }

    public MoreThanOneDataFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
