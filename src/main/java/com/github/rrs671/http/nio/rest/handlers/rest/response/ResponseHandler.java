package com.github.rrs671.http.nio.rest.handlers.rest.response;

import com.github.rrs671.http.nio.rest.http.Response;
import com.github.rrs671.http.nio.rest.utils.Tuple;

import java.util.Optional;

public interface ResponseHandler<K, T> {

    void addResponse(K key, Response<T> response);

    boolean hasResponse();

    Optional<Tuple<K, T>> consume();

    int getInactiveTimeInSeconds();

    int getWorkers();

}
