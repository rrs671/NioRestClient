package com.github.rrs671.http.nio.rest.client.request;

import com.github.rrs671.http.nio.rest.client.factory.RestClientFactory;
import com.github.rrs671.http.nio.rest.exceptions.CommunicateException;
import com.github.rrs671.http.nio.rest.exceptions.HttpException;
import com.github.rrs671.http.nio.rest.exceptions.ProcessException;
import com.github.rrs671.http.nio.rest.utils.HttpTimeoutParams;
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
    private final ExecutorService limitedThreadsExecutor;
    private final RestClient restClient;

    public RestRequest(HttpTimeoutParams httpTimeoutParams) {
        this.restClient = RestClientFactory.create(httpTimeoutParams.getConnTimeout(), httpTimeoutParams.getReadTimeout());
        limitedThreadsExecutor = null;
    }


    public RestRequest(HttpTimeoutParams httpTimeoutParams, int currentRequests) {
        this.restClient = RestClientFactory.create(httpTimeoutParams.getConnTimeout(), httpTimeoutParams.getReadTimeout());
        limitedThreadsExecutor = new ThreadPoolExecutor(
                currentRequests, currentRequests, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), Thread.ofVirtual().factory()
        );
    }

    public RestRequest(HttpTimeoutParams httpTimeoutParams, int currentRequests, int delayRequestsInSeconds) {
        this.restClient = RestClientFactory.create(httpTimeoutParams.getConnTimeout(), httpTimeoutParams.getReadTimeout());
        this.limitedThreadsExecutor = Executors.newScheduledThreadPool(currentRequests);
    }

    public <T> CompletableFuture<T> get(RequestParams params, Class<T> clazz) {
        String url = buildUrl(params.getBaseUrl(), params.getPaths(), params.getQueryParams());

        ExecutorService executor = getExecutorService();
        return CompletableFuture.supplyAsync(() -> executeRequest(() -> {
                RestClient.RequestHeadersSpec<?> spec = restClient.get().uri(url);

                if (Objects.nonNull(params.getHeaders())) {
                    params.getHeaders().forEach(spec::header);
                }

                return spec.retrieve().body(clazz);
            }
        ), executor);
    }

    public <T, R> CompletableFuture<T> post(RequestParams params, R body, Class<T> clazz) {
        String url = buildUrl(params.getBaseUrl(), params.getPaths(), params.getQueryParams());

        ExecutorService executor = getExecutorService();

        return CompletableFuture.supplyAsync(() -> executeRequest(() -> {
                RestClient.RequestBodySpec spec = restClient.post().uri(url);

                if (Objects.nonNull(params.getHeaders())) {
                    params.getHeaders().forEach(spec::header);
                }

                return spec.body(body).retrieve().body(clazz);
            }
        ), executor);
    }

    public <T, R> CompletableFuture<T> put(RequestParams params, R body, Class<T> clazz) {
        String url = buildUrl(params.getBaseUrl(), params.getPaths(), params.getQueryParams());

        ExecutorService executor = getExecutorService();

        return CompletableFuture.supplyAsync(() -> executeRequest(() -> {
                RestClient.RequestBodySpec spec = restClient.put().uri(url);

                if (Objects.nonNull(params.getHeaders())) {
                    params.getHeaders().forEach(spec::header);
                }

                return spec.body(body).retrieve().body(clazz);
            }
        ), executor);
    }

    public <T, R> CompletableFuture<T> patch(RequestParams params, R body, Class<T> clazz) {
        String url = buildUrl(params.getBaseUrl(), params.getPaths(), params.getQueryParams());

        ExecutorService executor = getExecutorService();

        return CompletableFuture.supplyAsync(() -> executeRequest(() -> {
                RestClient.RequestBodySpec spec = restClient.patch().uri(url);

                if (Objects.nonNull(params.getHeaders())) {
                    params.getHeaders().forEach(spec::header);
                }

                return spec.body(body).retrieve().body(clazz);
            }
        ), executor);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public CompletableFuture<Void> delete(RequestParams params) {
        String url = buildUrl(params.getBaseUrl(), params.getPaths(), params.getQueryParams());

        ExecutorService executor = getExecutorService();

        return CompletableFuture.supplyAsync(() -> executeRequest(() -> {
            RestClient.RequestHeadersSpec<?> spec = restClient.delete().uri(url);

            if (Objects.nonNull(params.getHeaders())) {
                params.getHeaders().forEach(spec::header);
            }

            spec.retrieve();
            return null;
        }), executor);
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

    private <T> T executeRequest(RequestExecutor<T> executor) {
        try {
            return executor.execute();
        } catch (HttpStatusCodeException e) {
            throw new HttpException(e);
        } catch (ResourceAccessException e) {
            throw new CommunicateException(e.getMessage()) ;
        } catch (Exception e) {
            throw new ProcessException(e.getMessage());
        }
    }

    private ExecutorService getExecutorService() {
        return (Objects.isNull(limitedThreadsExecutor)) ? globalExecutor : limitedThreadsExecutor;
    }

    @Override
    public void close() {
        if (Objects.nonNull(limitedThreadsExecutor)) {
            limitedThreadsExecutor.shutdown();
        }
    }
}
