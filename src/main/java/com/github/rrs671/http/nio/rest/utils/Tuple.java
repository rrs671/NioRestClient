package com.github.rrs671.http.nio.rest.utils;

import com.github.rrs671.http.nio.rest.http.Response;

public class Tuple<K, T> {

    private final K key;
    private final Response<T> response;

    public Tuple(K key, Response<T> response) {
        this.key = key;
        this.response = response;
    }

    public K getKey() {
        return key;
    }

    public Response<T> getResponse() {
        return response;
    }

}
