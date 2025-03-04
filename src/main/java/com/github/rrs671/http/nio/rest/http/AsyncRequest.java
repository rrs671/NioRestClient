package com.github.rrs671.http.nio.rest.http;

import com.github.rrs671.http.nio.rest.utils.ResponseUtils;

import java.util.concurrent.CompletableFuture;

/**
 * This classes represents the async response that will be completed in the future.
 *
 * @since 2.4.0
 */
public class AsyncRequest<T> {

    private final CompletableFuture<T> future;

    public AsyncRequest(CompletableFuture<T> future) {
        this.future = future;
    }

    public Response<T> getResponse() {
        return ResponseUtils.getResult(this.future);
    }

    public CompletableFuture<T> toCompletableFuture() {
        return this.future;
    }

}
