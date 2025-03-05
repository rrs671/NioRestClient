package com.github.rrs671.http.nio.rest.client.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rrs671.http.nio.rest.client.enums.VerbsEnum;
import com.github.rrs671.http.nio.rest.client.factory.RestClientFactory;
import com.github.rrs671.http.nio.rest.client.request.strategy.request.Request;
import com.github.rrs671.http.nio.rest.client.request.strategy.request.strategies.*;
import com.github.rrs671.http.nio.rest.http.AsyncRequest;
import com.github.rrs671.http.nio.rest.utils.AsyncExecutorUtils;
import com.github.rrs671.http.nio.rest.utils.ClientParams;
import com.github.rrs671.http.nio.rest.utils.RequestParams;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

public class RestRequest {

    private ExecutorService globalExecutor;
    private final RestClient restClient;
    private ClientParams clientParams;
    private Semaphore semaphore;

    public RestRequest(ClientParams clientParams) {
        this.restClient = RestClientFactory.create(clientParams.getConnTimeout(), clientParams.getReadTimeout(), null);
        init(clientParams);
    }

    public RestRequest(ClientParams clientParams, ObjectMapper objectMapper) {
        this.restClient = RestClientFactory.create(clientParams.getConnTimeout(), clientParams.getReadTimeout(), objectMapper);
        init(clientParams);
    }

    private void init(ClientParams clientParams) {
        this.clientParams = clientParams;
        this.globalExecutor = AsyncExecutorUtils.getGlobalExecutorInstance();

        if (clientParams.getMaxConcurrentRequests() > 0) {
            this.semaphore = new Semaphore(clientParams.getMaxConcurrentRequests(), true);
        } else {
            this.semaphore = new Semaphore(Integer.MAX_VALUE, true);
        }
    }


    public <T> AsyncRequest<T> get(RequestParams params, Class<T> clazz) {
        AsyncExecutorUtils.incrementRequest();

        String url = buildUrl(params.getBaseUrl(), params.getPaths(), params.getQueryParams());

        GetRequestStrategy get = (GetRequestStrategy) Request.getVerbStrategy(VerbsEnum.GET);
        CompletableFuture<T> future = get.getRequest(globalExecutor, semaphore, restClient, params, clazz, url, clientParams, isScheduled());

        return processGetResponse(future);
    }

    private <T> AsyncRequest<T> processGetResponse(CompletableFuture<T> future) {
        return AsyncExecutorUtils.returnAsyncResponse(future, globalExecutor);
    }

    public <T, R> AsyncRequest<T> post(RequestParams params, R body, Class<T> clazz) {
        AsyncExecutorUtils.incrementRequest();

        String url = buildUrl(params.getBaseUrl(), params.getPaths(), params.getQueryParams());

        PostRequestStrategy post = (PostRequestStrategy) Request.getVerbStrategy(VerbsEnum.POST);
        CompletableFuture<T> future = post.postRequest(globalExecutor, semaphore, restClient, params, body, clazz, url, clientParams, isScheduled());

        return processPostResponse(future);
    }

    private <T> AsyncRequest<T> processPostResponse(CompletableFuture<T> future) {
        return AsyncExecutorUtils.returnAsyncResponse(future, globalExecutor);
    }

    public <T, R> AsyncRequest<T> put(RequestParams params, R body, Class<T> clazz) {
        AsyncExecutorUtils.incrementRequest();

        String url = buildUrl(params.getBaseUrl(), params.getPaths(), params.getQueryParams());

        PutRequestStrategy put = (PutRequestStrategy) Request.getVerbStrategy(VerbsEnum.PUT);
        CompletableFuture<T> future = put.putRequest(globalExecutor, semaphore, restClient, params, body, clazz, url, clientParams, isScheduled());

        return processPutResponse(future);
    }

    private <T> AsyncRequest<T> processPutResponse(CompletableFuture<T> future) {
        return AsyncExecutorUtils.returnAsyncResponse(future, globalExecutor);
    }

    public <T, R> AsyncRequest<T> patch(RequestParams params, R body, Class<T> clazz) {
        AsyncExecutorUtils.incrementRequest();

        String url = buildUrl(params.getBaseUrl(), params.getPaths(), params.getQueryParams());

        PatchRequestStrategy patch = (PatchRequestStrategy) Request.getVerbStrategy(VerbsEnum.PATCH);
        CompletableFuture<T> future = patch.patchRequest(globalExecutor, semaphore, restClient, params, body, clazz, url, clientParams, isScheduled());

        return processPatchResponse(future);
    }

    private <T> AsyncRequest<T> processPatchResponse(CompletableFuture<T> future) {
        return AsyncExecutorUtils.returnAsyncResponse(future, globalExecutor);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public AsyncRequest<Void> delete(RequestParams params) {
        AsyncExecutorUtils.incrementRequest();

        String url = buildUrl(params.getBaseUrl(), params.getPaths(), params.getQueryParams());

        DeleteRequestStrategy delete = (DeleteRequestStrategy) Request.getVerbStrategy(VerbsEnum.DELETE);
        CompletableFuture<Void> future = delete.deleteRequest(globalExecutor, semaphore, restClient, params, url, clientParams, isScheduled());

        return processDeleteResponse(future);
    }

    private AsyncRequest<Void> processDeleteResponse(CompletableFuture<Void> future) {
        return AsyncExecutorUtils.returnAsyncResponse(future, globalExecutor);
    }

    private String buildUrl(String baseUrl, List<String> paths, Map<String, String> queryParams) {
        StringBuilder url = new StringBuilder(baseUrl);

        if (paths != null && !paths.isEmpty()) {
            url.append("/").append(String.join("/", paths));
        }

        if (queryParams != null && !queryParams.isEmpty()) {
            url.append("?").append(queryParams.entrySet().stream()
                    .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "=" +
                            URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&")));
        }

        return url.toString();
    }

    private boolean isScheduled() {
        return this.clientParams.getDelayInMilliSeconds() > 0L;
    }

}
