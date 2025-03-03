package com.github.rrs671.http.nio.rest.http;

import com.github.rrs671.http.nio.rest.utils.ResponseUtils;

import java.util.concurrent.CompletableFuture;

public class Request<T> {

    private final CompletableFuture<T> future;

    public Request(CompletableFuture<T> future) {
        this.future = future;
    }

    public Response<T> getResponse() {
        return ResponseUtils.getResult(this.future);
    }

    public CompletableFuture<T> toCompletableFuture() {
        return this.future;
    }

}
