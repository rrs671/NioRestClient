package com.github.rrs671.http.nio.rest.handlers.rest.request.delete;

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
 * using this class is possible to create multiples DELETE Requests and receive the
 * responses from the ResponseHandles asynchronously
 * @param <K> key from requests
 * @since 2.4.0
 */
public class DeleteRestHandler<K> extends RestHandler<K, Void> {

    private final Queue<Map.Entry<K, AsyncRequest<Void>>> pendingAsyncRequests;

    private final RestRequest restRequest;

    public DeleteRestHandler(NioRestClient client, ClientParams clientParams, ResponseHandler<K, Void> responseHandler) {
        this.restRequest = client.rest(clientParams);
        this.pendingAsyncRequests = new LinkedBlockingQueue<>();
        super.registerResponseHandler(pendingAsyncRequests, responseHandler);
    }

    public void doDelete(K key, RequestParams requestParams) {
        AsyncRequest<Void> asyncRequest = restRequest.delete(requestParams);
        pendingAsyncRequests.offer(new AbstractMap.SimpleEntry<>(key, asyncRequest));
    }

}