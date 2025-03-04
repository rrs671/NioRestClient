package com.github.rrs671.http.nio.rest.handlers.rest;

import com.github.rrs671.http.nio.rest.handlers.rest.response.ResponseHandler;
import com.github.rrs671.http.nio.rest.http.AsyncRequest;
import com.github.rrs671.http.nio.rest.utils.AsyncExecutorUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public abstract class RestHandler<K, T> {

    protected void registerResponseHandler(Queue<Map.Entry<K, AsyncRequest<T>>> pendingAsyncRequests, ResponseHandler<K, T> responseHandler) {
        startResponseProcessing(pendingAsyncRequests, responseHandler, responseHandler.getInactiveTimeInSeconds(), responseHandler.getWorkers());
    }

    protected void startResponseProcessing(Queue<Map.Entry<K, AsyncRequest<T>>> pendingAsyncRequests, ResponseHandler<K, T> responseHandler,
                                        int inactiveTimeInSeconds, int workers) {
        ExecutorService executor = AsyncExecutorUtils.getGlobalExecutorInstance();

        for (int i = 0; i < workers; i++) {
            CompletableFuture.runAsync(() -> {
                while (true) {
                    if (pendingAsyncRequests.isEmpty()) {
                        try {
                            Thread.sleep(inactiveTimeInSeconds * 1000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        continue;
                    }

                    Map.Entry<K, AsyncRequest<T>> requestEntry = pendingAsyncRequests.poll();

                    if (Objects.nonNull(requestEntry)) {
                        executor.submit(() -> responseHandler.addResponse(requestEntry.getKey(), requestEntry.getValue().getResponse()));
                    }
                }
            }, executor);
        }
    }

}
