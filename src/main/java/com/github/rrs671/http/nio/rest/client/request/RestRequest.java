package com.github.rrs671.http.nio.rest.client.request;

import com.github.rrs671.http.nio.rest.client.NioRestClient;
import com.github.rrs671.http.nio.rest.client.enums.VerbsEnum;
import com.github.rrs671.http.nio.rest.client.factory.RestClientFactory;
import com.github.rrs671.http.nio.rest.client.request.strategy.request.Request;
import com.github.rrs671.http.nio.rest.client.request.strategy.request.strategies.*;
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

    private final ExecutorService globalExecutor;
    private final RestClient restClient;
    private final ClientParams clientParams;
    private final Semaphore semaphore;

    public RestRequest(ClientParams clientParams) {
        this.restClient = RestClientFactory.create(clientParams.getConnTimeout(), clientParams.getReadTimeout());
        this.clientParams = clientParams;
        this.globalExecutor = AsyncExecutorUtils.getGlobalExecutorInstance();

        if (clientParams.getMaxConcurrentRequests() > 0) {
            this.semaphore = new Semaphore(clientParams.getMaxConcurrentRequests(), true);
        } else {
            this.semaphore = new Semaphore(Integer.MAX_VALUE, true);
        }
    }

    public <T> com.github.rrs671.http.nio.rest.http.Request<T> get(RequestParams params, Class<T> clazz) {
        AsyncExecutorUtils.incrementRequest();

        String url = buildUrl(params.getBaseUrl(), params.getPaths(), params.getQueryParams());

        GetRequestStrategy get = (GetRequestStrategy) Request.getVerbStrategy(VerbsEnum.GET);
        CompletableFuture<T> future = get.getRequest(globalExecutor, semaphore, restClient, params, clazz, url, clientParams, isScheduled());

        return processGetResponse(future);
    }

    private <T> com.github.rrs671.http.nio.rest.http.Request<T> processGetResponse(CompletableFuture<T> future) {
        return AsyncExecutorUtils.returnAsyncResponse(future, globalExecutor, semaphore, clientParams, isScheduled());
    }

    public <T, R> com.github.rrs671.http.nio.rest.http.Request<T> post(RequestParams params, R body, Class<T> clazz) {
        AsyncExecutorUtils.incrementRequest();

        String url = buildUrl(params.getBaseUrl(), params.getPaths(), params.getQueryParams());

        PostRequestStrategy post = (PostRequestStrategy) Request.getVerbStrategy(VerbsEnum.POST);
        CompletableFuture<T> future = post.postRequest(globalExecutor, semaphore, restClient, params, body, clazz, url, clientParams, isScheduled());

        return processPostResponse(future);
    }

    private <T> com.github.rrs671.http.nio.rest.http.Request<T> processPostResponse(CompletableFuture<T> future) {
        return AsyncExecutorUtils.returnAsyncResponse(future, globalExecutor, semaphore, clientParams, isScheduled());
    }

    public <T, R> com.github.rrs671.http.nio.rest.http.Request<T> put(RequestParams params, R body, Class<T> clazz) {
        AsyncExecutorUtils.incrementRequest();

        String url = buildUrl(params.getBaseUrl(), params.getPaths(), params.getQueryParams());

        PutRequestStrategy put = (PutRequestStrategy) Request.getVerbStrategy(VerbsEnum.PUT);
        CompletableFuture<T> future = put.putRequest(globalExecutor, semaphore, restClient, params, body, clazz, url, clientParams, isScheduled());

        return processPutResponse(future);
    }

    private <T> com.github.rrs671.http.nio.rest.http.Request<T> processPutResponse(CompletableFuture<T> future) {
        return AsyncExecutorUtils.returnAsyncResponse(future, globalExecutor, semaphore, clientParams, isScheduled());
    }

    public <T, R> com.github.rrs671.http.nio.rest.http.Request<T> patch(RequestParams params, R body, Class<T> clazz) {
        AsyncExecutorUtils.incrementRequest();

        String url = buildUrl(params.getBaseUrl(), params.getPaths(), params.getQueryParams());

        PatchRequestStrategy patch = (PatchRequestStrategy) Request.getVerbStrategy(VerbsEnum.PATCH);
        CompletableFuture<T> future = patch.patchRequest(globalExecutor, semaphore, restClient, params, body, clazz, url, clientParams, isScheduled());

        return processPatchResponse(future);
    }

    private <T> com.github.rrs671.http.nio.rest.http.Request<T> processPatchResponse(CompletableFuture<T> future) {
        return AsyncExecutorUtils.returnAsyncResponse(future, globalExecutor, semaphore, clientParams, isScheduled());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public com.github.rrs671.http.nio.rest.http.Request<Void> delete(RequestParams params) {
        AsyncExecutorUtils.incrementRequest();

        String url = buildUrl(params.getBaseUrl(), params.getPaths(), params.getQueryParams());

        DeleteRequestStrategy delete = (DeleteRequestStrategy) Request.getVerbStrategy(VerbsEnum.DELETE);
        CompletableFuture<Void> future = delete.deleteRequest(globalExecutor, semaphore, restClient, params, url, clientParams, isScheduled());

        return processDeleteResponse(future);
    }

    private com.github.rrs671.http.nio.rest.http.Request<Void> processDeleteResponse(CompletableFuture<Void> future) {
        return AsyncExecutorUtils.returnAsyncResponse(future, globalExecutor, semaphore, clientParams, isScheduled());
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
        return this.clientParams.getDelay() > 0;
    }

}
