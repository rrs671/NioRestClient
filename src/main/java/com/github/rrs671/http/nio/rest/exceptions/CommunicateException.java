package com.github.rrs671.http.nio.rest.exceptions;

/**
 * A class for failures on communications
 *
 * @since 1.0.0
 */
public class CommunicateException extends RuntimeException {

    public CommunicateException() {
        super();
    }

    public CommunicateException(String message) {
        super(message);
    }

    public CommunicateException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommunicateException(Throwable cause) {
        super(cause);
    }

    protected CommunicateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
