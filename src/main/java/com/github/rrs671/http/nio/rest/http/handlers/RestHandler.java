package com.github.rrs671.http.nio.rest.http.handlers;

import com.github.rrs671.http.nio.rest.client.NioRestClient;
import com.github.rrs671.http.nio.rest.client.enums.VerbsEnum;
import com.github.rrs671.http.nio.rest.utils.ClientParams;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RestHandler<T> {

    private final ExecutorService handler;

    private final NioRestClient client;
    private final ClientParams clientParams;
    private final Class<T> clazz;
    private final VerbsEnum verb;

    public RestHandler(NioRestClient client, ClientParams clientParams, VerbsEnum verb, Class<T> clazz) {
        this.handler = Executors.newVirtualThreadPerTaskExecutor();

        this.client = client;
        this.clientParams = clientParams;
        this.verb = verb;
        this.clazz = clazz;
    }



}
