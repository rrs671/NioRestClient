package com.github.rrs671.http.nio.rest.client.request;

import com.github.rrs671.http.nio.rest.client.enums.VerbsEnum;
import com.github.rrs671.http.nio.rest.client.factory.RestClientFactory;
import com.github.rrs671.http.nio.rest.client.request.strategy.scheduled_request.ScheduledRequest;
import com.github.rrs671.http.nio.rest.client.request.strategy.scheduled_request.strategies.*;
import com.github.rrs671.http.nio.rest.exceptions.CommunicateException;
import com.github.rrs671.http.nio.rest.exceptions.HttpException;
import com.github.rrs671.http.nio.rest.exceptions.ProcessException;
import com.github.rrs671.http.nio.rest.utils.AsyncExecutorUtils;
import com.github.rrs671.http.nio.rest.utils.NioRestClientParams;
import com.github.rrs671.http.nio.rest.utils.RequestParams;
import org.springframework.web.client.*;

import java.io.Closeable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class RestRequest implements Closeable {

    private static final ExecutorService globalExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private ExecutorService limitedThreadsExecutor;
    private final ExecutorService defaultExecutor;

    private final RestClient restClient;
    private final NioRestClientParams nioRestClientParams;
    private Semaphore semaphore;

    public RestRequest(NioRestClientParams nioRestClientParams) {
        this.restClient = RestClientFactory.create(nioRestClientParams.getConnTimeout(), nioRestClientParams.getReadTimeout());
        this.nioRestClientParams = nioRestClientParams;


        if (nioRestClientParams.getMaxConcurrentRequests() > 0) {
            limitedThreadsExecutor = new ThreadPoolExecutor(
                    nioRestClientParams.getMaxConcurrentRequests(), nioRestClientParams.getMaxConcurrentRequests(), 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(), Thread.ofVirtual().factory()
            );
        }

        if (isScheduled()) {
            this.semaphore = new Semaphore(nioRestClientParams.getMaxConcurrentRequests());
        }

        this.defaultExecutor = AsyncExecutorUtils.getExecutorService(limitedThreadsExecutor, globalExecutor);
    }

    public <T> CompletableFuture<T> get(RequestParams params, Class<T> clazz) {
        String url = buildUrl(params.getBaseUrl(), params.getPaths(), params.getQueryParams());

        if (isScheduled()) {
            return scheduledGet(params, clazz, url);
        }

        return AsyncExecutorUtils.asyncRequest(defaultExecutor, () -> {
            System.out.println("Req...");
            RestClient.RequestHeadersSpec<?> spec = restClient.get().uri(url);

            if (Objects.nonNull(params.getHeaders())) {
                params.getHeaders().forEach(spec::header);
            }

            return spec.retrieve().body(clazz);
        });
    }

    private <T> CompletableFuture<T> scheduledGet(RequestParams params, Class<T> clazz, String url) {
        GetScheduledRequestStrategy get = (GetScheduledRequestStrategy) ScheduledRequest.getVerbExecutor(VerbsEnum.GET);
        Future<T> future = get.getScheduled(limitedThreadsExecutor, semaphore, restClient, params, clazz, url);
        return AsyncExecutorUtils.returnAsyncScheduledResponse(future, globalExecutor, semaphore, nioRestClientParams);
    }

    public <T, R> CompletableFuture<T> post(RequestParams params, R body, Class<T> clazz) {
        String url = buildUrl(params.getBaseUrl(), params.getPaths(), params.getQueryParams());

        if (isScheduled()) {
            return scheduledPost(params, body, clazz, url);
        }

        return AsyncExecutorUtils.asyncRequest(defaultExecutor, () -> {
            RestClient.RequestBodySpec spec = restClient.post().uri(url);

            if (Objects.nonNull(params.getHeaders())) {
                params.getHeaders().forEach(spec::header);
            }

            return spec.body(body).retrieve().body(clazz);
        });
    }

    private <T, R> CompletableFuture<T> scheduledPost(RequestParams params, R body, Class<T> clazz, String url) {
        PostScheduledRequestStrategy post = (PostScheduledRequestStrategy) ScheduledRequest.getVerbExecutor(VerbsEnum.POST);
        Future<T> future = post.postScheduled(limitedThreadsExecutor, semaphore, restClient, params, body, clazz, url);
        return AsyncExecutorUtils.returnAsyncScheduledResponse(future, globalExecutor, semaphore, nioRestClientParams);
    }

    public <T, R> CompletableFuture<T> put(RequestParams params, R body, Class<T> clazz) {
        String url = buildUrl(params.getBaseUrl(), params.getPaths(), params.getQueryParams());

        if (isScheduled()) {
            return scheduledPut(params, body, clazz, url);
        }

        return AsyncExecutorUtils.asyncRequest(defaultExecutor, () -> {
            RestClient.RequestBodySpec spec = restClient.put().uri(url);

            if (Objects.nonNull(params.getHeaders())) {
                params.getHeaders().forEach(spec::header);
            }

            return spec.body(body).retrieve().body(clazz);
        });
    }

    private <T, R> CompletableFuture<T> scheduledPut(RequestParams params, R body, Class<T> clazz, String url) {
        PutScheduledRequestStrategy put = (PutScheduledRequestStrategy) ScheduledRequest.getVerbExecutor(VerbsEnum.PUT);
        Future<T> future = put.putScheduled(limitedThreadsExecutor, semaphore, restClient, params, body, clazz, url);
        return AsyncExecutorUtils.returnAsyncScheduledResponse(future, globalExecutor, semaphore, nioRestClientParams);
    }

    public <T, R> CompletableFuture<T> patch(RequestParams params, R body, Class<T> clazz) {
        String url = buildUrl(params.getBaseUrl(), params.getPaths(), params.getQueryParams());

        if (isScheduled()) {
            return scheduledPatch(params, body, clazz, url);
        }

        return AsyncExecutorUtils.asyncRequest(defaultExecutor, () -> {
            RestClient.RequestBodySpec spec = restClient.patch().uri(url);

            if (Objects.nonNull(params.getHeaders())) {
                params.getHeaders().forEach(spec::header);
            }

            return spec.body(body).retrieve().body(clazz);
        });
    }

    private <T, R> CompletableFuture<T> scheduledPatch(RequestParams params, R body, Class<T> clazz, String url) {
        PatchScheduledRequestStrategy patch = (PatchScheduledRequestStrategy) ScheduledRequest.getVerbExecutor(VerbsEnum.PATCH);
        Future<T> future = patch.patchScheduled(limitedThreadsExecutor, semaphore, restClient, params, body, clazz, url);
        return AsyncExecutorUtils.returnAsyncScheduledResponse(future, globalExecutor, semaphore, nioRestClientParams);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public CompletableFuture<Void> delete(RequestParams params) {
        String url = buildUrl(params.getBaseUrl(), params.getPaths(), params.getQueryParams());

        if (isScheduled()) {
            return scheduledDelete(params, url);
        }

        return AsyncExecutorUtils.asyncRequest(defaultExecutor, () -> {
            RestClient.RequestHeadersSpec<?> spec = restClient.delete().uri(url);

            if (Objects.nonNull(params.getHeaders())) {
                params.getHeaders().forEach(spec::header);
            }

            spec.retrieve();
            return null;
        });
    }

    private CompletableFuture<Void> scheduledDelete(RequestParams params, String url) {
        DeleteScheduledRequestStrategy delete = (DeleteScheduledRequestStrategy) ScheduledRequest.getVerbExecutor(VerbsEnum.DELETE);
        Future<Void> future = delete.deleteScheduled(limitedThreadsExecutor, semaphore, restClient, params, url);
        return AsyncExecutorUtils.returnAsyncScheduledResponse(future, globalExecutor, semaphore, nioRestClientParams);
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

    @Override
    public void close() {
        if (Objects.nonNull(limitedThreadsExecutor)) {
            limitedThreadsExecutor.shutdown();
        }
    }
}
