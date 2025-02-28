package br.dev.ag.http.nio.rest.client.request;

@FunctionalInterface
public interface RequestExecutor<T> {

    T execute();

}
