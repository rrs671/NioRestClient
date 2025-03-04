package com.github.rrs671.http.nio.rest.handlers.rest.response;

import com.github.rrs671.http.nio.rest.http.Response;
import com.github.rrs671.http.nio.rest.utils.Tuple;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/***
 * A class designed to recovery asynchronously requests response from a Rest Handle.
 * @param <K> key from requests
 * @param <T> return type
 * @since 2.4.0
 */
public class ResponseHandlerImpl<K, T> implements ResponseHandler<K,T> {

    private final Queue<Map.Entry<K, Response<T>>> readyRequests;
    private final int inactiveTimeInSeconds;
    private final int workers;

    public ResponseHandlerImpl(int inactiveTimeInSeconds, int workers) {
        readyRequests = new LinkedBlockingQueue<>();
        this.inactiveTimeInSeconds = inactiveTimeInSeconds;
        this.workers = workers;
    }

    @Override
    public boolean hasResponse() {
        return !readyRequests.isEmpty();
    }

    @Override
    public void addResponse(K key, Response<T> response) {
        this.readyRequests.offer(new AbstractMap.SimpleEntry<>(key, response));
    }

    @Override
    public Optional<Tuple<K, T>> consume() {
        Map.Entry<K, Response<T>> polled = readyRequests.poll();

        if (Objects.isNull(polled)) {
            return Optional.empty();
        }

        return Optional.of(new Tuple<>(polled.getKey(), polled.getValue()));
    }

    @Override
    public int getInactiveTimeInSeconds() {
        return inactiveTimeInSeconds;
    }

    @Override
    public int getWorkers() {
        return workers;
    }

}
