package br.dev.ag.http.nio.rest.client.request;

import br.dev.ag.http.nio.rest.utils.RequestParams;
import br.dev.ag.http.nio.rest.client.factory.RestClientFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

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

    public RestRequest(int timeoutInSeconds) {
        this.restClient = RestClientFactory.create(timeoutInSeconds);
        limitedThreadsExecutor = null;
    }

    public RestRequest(int timeoutInSeconds, int currentRequests) {
        this.restClient = RestClientFactory.create(timeoutInSeconds);
        limitedThreadsExecutor = new ThreadPoolExecutor(
                currentRequests, currentRequests, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), Thread.ofVirtual().factory()
        );
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
            }, url
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
            }, url
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
            }, url
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
            }, url
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
        }, url), executor);
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

    private <T> T executeRequest(RequestExecutor<T> executor, String url) {
        try {
            return executor.execute();
        } catch (HttpClientErrorException e) {
            HttpStatusCode statusCode = e.getStatusCode();
            throw e;
        } catch (ResourceAccessException e) {
            throw e;
        } catch (Exception e) {
            throw e;
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
