package com.github.rrs671.http.nio.rest.exceptions;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpStatusCodeException;

/**
 * A generic class http status exceptions
 *
 * @since 1.0.0
 */
public class HttpException extends RuntimeException {

    private final HttpStatusCode statusCode;

    public HttpException(HttpStatusCodeException e) {
        super();
        this.statusCode = e.getStatusCode();
    }

    public HttpStatusCode getStatusCode() {
        return this.statusCode;
    }
}
