package com.github.rrs671.http.nio.rest.client.request;

import com.github.rrs671.http.nio.rest.client.enums.VerbsEnum;
import com.github.rrs671.http.nio.rest.client.factory.RestClientFactory;
import com.github.rrs671.http.nio.rest.client.request.strategy.request.Request;
import com.github.rrs671.http.nio.rest.client.request.strategy.request.strategies.*;
import com.github.rrs671.http.nio.rest.utils.AsyncExecutorUtils;
import com.github.rrs671.http.nio.rest.utils.NioRestClientParams;
import com.github.rrs671.http.nio.rest.utils.RequestParams;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

public class RestRequest {

    private static final ExecutorService globalExecutor = Executors.newVirtualThreadPerTaskExecutor();

    private final RestClient restClient;
    private final NioRestClientParams nioRestClientParams;
    private final Semaphore semaphore;

    public RestRequest(NioRestClientParams nioRestClientParams) {
        this.restClient = RestClientFactory.create(nioRestClientParams.getConnTimeout(), nioRestClientParams.getReadTimeout());
        this.nioRestClientParams = nioRestClientParams;

        if (nioRestClientParams.getMaxConcurrentRequests() > 0) {
            this.semaphore = new Semaphore(nioRestClientParams.getMaxConcurrentRequests(), true);
        } else {
            this.semaphore = new Semaphore(Integer.MAX_VALUE, true);
        }
    }

    public <T> CompletableFuture<T> get(RequestParams params, Class<T> clazz) {
        String url = buildUrl(params.getBaseUrl(), params.getPaths(), params.getQueryParams());

        GetRequestStrategy get = (GetRequestStrategy) Request.getVerbStrategy(VerbsEnum.GET);
        CompletableFuture<T> future = get.getRequest(globalExecutor, semaphore, restClient, params, clazz, url);

        return processGetResponse(future);
    }

    private <T> CompletableFuture<T> processGetResponse(CompletableFuture<T> future) {
        return AsyncExecutorUtils.returnAsyncResponse(future, globalExecutor, semaphore, nioRestClientParams, isScheduled());
    }

    public <T, R> CompletableFuture<T> post(RequestParams params, R body, Class<T> clazz) {
        String url = buildUrl(params.getBaseUrl(), params.getPaths(), params.getQueryParams());

        PostRequestStrategy post = (PostRequestStrategy) Request.getVerbStrategy(VerbsEnum.POST);
        CompletableFuture<T> future = post.postRequest(globalExecutor, semaphore, restClient, params, body, clazz, url);

        return processPostResponse(future);
    }

    private <T> CompletableFuture<T> processPostResponse(CompletableFuture<T> future) {
        return AsyncExecutorUtils.returnAsyncResponse(future, globalExecutor, semaphore, nioRestClientParams, isScheduled());
    }

    public <T, R> CompletableFuture<T> put(RequestParams params, R body, Class<T> clazz) {
        String url = buildUrl(params.getBaseUrl(), params.getPaths(), params.getQueryParams());

        PutRequestStrategy put = (PutRequestStrategy) Request.getVerbStrategy(VerbsEnum.PUT);
        CompletableFuture<T> future = put.putRequest(globalExecutor, semaphore, restClient, params, body, clazz, url);

        return processPutResponse(future);
    }

    private <T> CompletableFuture<T> processPutResponse(CompletableFuture<T> future) {
        return AsyncExecutorUtils.returnAsyncResponse(future, globalExecutor, semaphore, nioRestClientParams, isScheduled());
    }

    public <T, R> CompletableFuture<T> patch(RequestParams params, R body, Class<T> clazz) {
        String url = buildUrl(params.getBaseUrl(), params.getPaths(), params.getQueryParams());

        PatchRequestStrategy patch = (PatchRequestStrategy) Request.getVerbStrategy(VerbsEnum.PATCH);
        CompletableFuture<T> future = patch.patchRequest(globalExecutor, semaphore, restClient, params, body, clazz, url);

        return processPatchResponse(future);
    }

    private <T> CompletableFuture<T> processPatchResponse(CompletableFuture<T> future) {
        return AsyncExecutorUtils.returnAsyncResponse(future, globalExecutor, semaphore, nioRestClientParams, isScheduled());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public CompletableFuture<Void> delete(RequestParams params) {
        String url = buildUrl(params.getBaseUrl(), params.getPaths(), params.getQueryParams());

        DeleteRequestStrategy delete = (DeleteRequestStrategy) Request.getVerbStrategy(VerbsEnum.DELETE);
        CompletableFuture<Void> future = delete.deleteRequest(globalExecutor, semaphore, restClient, params, url);

        return processDeleteResponse(future);
    }

    private CompletableFuture<Void> processDeleteResponse(CompletableFuture<Void> future) {
        return AsyncExecutorUtils.returnAsyncResponse(future, globalExecutor, semaphore, nioRestClientParams, isScheduled());
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
        return this.nioRestClientParams.getDelay() > 0 && nioRestClientParams.getMaxConcurrentRequests() > 0;
    }

}
