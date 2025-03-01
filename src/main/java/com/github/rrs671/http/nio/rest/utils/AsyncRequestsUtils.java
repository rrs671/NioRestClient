package com.github.rrs671.http.nio.rest.utils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * This classes has useful methods to manipulate async requests

 @since 2.0.0
 */
public abstract class AsyncRequestsUtils {

    private AsyncRequestsUtils() {}

    public static <T>  getMultiResult() {
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(list.toArray(new CompletableFuture[0]));

        CompletableFuture<List<String>> resultsFuture = allFutures.thenApply(v ->
                list.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList())
        );

        List<String> results = resultsFuture.join();
    }

}
