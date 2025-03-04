package com.github.rrs671.http.nio.rest.utils;

import com.github.rrs671.http.nio.rest.http.AsyncRequest;
import com.github.rrs671.http.nio.rest.http.Response;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * This classes has useful methods to manipulate async requests
 *
 * @since 2.0.1
 */
public abstract class ResponseUtils {

    private ResponseUtils() {}

    /**
     * Returns the request response
     *
     * @param future future to be processed and get the request response
     * @return a Response instance
     */
    public static <T> Response<T> getResult(CompletableFuture<T> future) {
        return future.thenApply(Response::new).exceptionally(Response::new).join();
    }

    /**
     * Returns all the requests responses
     *
     * @param asyncRequests futures to be processed and get the requests responses
     * @return a List of Response instance
     */
    public static <T> List<Response<T>> getMultiResult(List<AsyncRequest<T>> asyncRequests) {
        return (AsyncExecutorUtils.isParalell() ? asyncRequests.parallelStream() : asyncRequests.stream())
                .map(AsyncRequest::getResponse)
                .toList();
    }

    /**
     * Returns all the requests responses and returns a Map with the key informed on param and response from request
     *
     * @param requests futures to be processed and get the requests responses
     * @return a Map with the response Key and Response instance
     */
    public static <T, K> Map<K, Response<T>> getMultiResult(Map<K, AsyncRequest<T>> requests) {
        return (AsyncExecutorUtils.isParalell() ? requests.entrySet().parallelStream() : requests.entrySet().stream())
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().getResponse()))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }

}
