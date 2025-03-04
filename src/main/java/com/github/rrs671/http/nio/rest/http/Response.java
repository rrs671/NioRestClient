package com.github.rrs671.http.nio.rest.http;

import com.github.rrs671.http.nio.rest.exceptions.HttpException;
import com.github.rrs671.http.nio.rest.exceptions.ResponseException;
import org.springframework.http.HttpStatusCode;

import java.util.Optional;

/**
 * This classes represents the request response. To have a more safety, use also the ResponseUtils class.
 * If a response is completed with success (no error and success response code 2XX) the result can be access using
 * the method getSucessResult. If this method is access for a error result a ResponseException will be thrown.
 * Using this class is possible to verify if this response is a Error, Success. On error cases there will be always a
 * error message and if it is a http response error the status code will be available.
 *
 * @since 2.0.1
 */
public class Response<T> {

    private final T result;
    private final boolean success;

    private HttpStatusCode statusCode;
    private String message;

    public Response(T result) {
        this.result = result;
        success = true;
    }

    public Response(Throwable throwable) {
        if (throwable.getCause() != null && throwable.getCause().getCause() != null) {
            message = throwable.getCause().getCause().getMessage();
        } else if (throwable.getCause() != null) {
            message = throwable.getCause().getMessage();
        } else {
            message = throwable.getMessage();
        }

        if (throwable instanceof HttpException) {
            statusCode = extractStatusCode(throwable);
        } else if (throwable.getCause() instanceof HttpException) {
            statusCode = extractStatusCode(throwable);
        } else if (throwable.getCause().getCause() instanceof HttpException) {
            statusCode = extractStatusCode(throwable.getCause());
        }

        result = null;
        success = false;
    }

    public Optional<T> getSuccessResult() {
        if (message != null) {
            throw new ResponseException("Success result is only available for http 2XX responses.");
        }

        return Optional.ofNullable(result);
    }

    public String getErrorMessage() {
        if (message == null) {
            throw new ResponseException("Error message is only available for unsuccessful requests");
        }

        return message;
    }

    public HttpStatusCode getErrorStatusCode() {
        if (statusCode == null) {
            throw new ResponseException("Error message is only available for unsuccessful requests by https error codes");
        }

        return statusCode;
    }

    public boolean isHttpResponseError() {
        return statusCode != null;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isError() {
        return !success;
    }

    private HttpStatusCode extractStatusCode(Throwable throwable) {
        if (throwable instanceof HttpException httpException) {
            return httpException.getStatusCode();
        }

        return ((HttpException) throwable.getCause()).getStatusCode();
    }

}
