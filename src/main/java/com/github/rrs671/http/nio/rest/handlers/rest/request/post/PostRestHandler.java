package com.github.rrs671.http.nio.rest.handlers.rest.request.post;

import com.github.rrs671.http.nio.rest.client.NioRestClient;
import com.github.rrs671.http.nio.rest.client.request.RestRequest;
import com.github.rrs671.http.nio.rest.handlers.rest.RestHandler;
import com.github.rrs671.http.nio.rest.handlers.rest.response.ResponseHandler;
import com.github.rrs671.http.nio.rest.http.AsyncRequest;
import com.github.rrs671.http.nio.rest.utils.ClientParams;
import com.github.rrs671.http.nio.rest.utils.RequestParams;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/***
 * A class designed to create a delete rest job.
 * using this class is possible to create multiples POST Requests and receive the
 * responses from the ResponseHandles asynchronously
 * @param <K> key from requests
 * @param <T> data return type
 * @param <B> body/payload value
 * @since 2.4.0
 */
public class PostRestHandler<K, T, B> extends RestHandler<K, T> {

    private final Queue<Map.Entry<K, AsyncRequest<T>>> pendingAsyncRequests;

    private final RestRequest restRequest;
    private final Class<T> clazz;

    public PostRestHandler(NioRestClient client, ClientParams clientParams, ResponseHandler<K, T> responseHandler, Class<T> clazz) {
        this.clazz = clazz;
        this.restRequest = client.rest(clientParams);
        this.pendingAsyncRequests = new LinkedBlockingQueue<>();
        super.registerResponseHandler(pendingAsyncRequests, responseHandler);
    }

    public void doPost(K key, RequestParams requestParams, B payload) {
        AsyncRequest<T> asyncRequest = restRequest.post(requestParams, payload, clazz);
        pendingAsyncRequests.offer(new AbstractMap.SimpleEntry<>(key, asyncRequest));
    }

}