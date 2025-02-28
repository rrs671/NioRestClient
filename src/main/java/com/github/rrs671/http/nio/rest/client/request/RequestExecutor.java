package com.github.rrs671.http.nio.rest.client.request;

@FunctionalInterface
public interface RequestExecutor<T> {

    T execute();

}
