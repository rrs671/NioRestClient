package com.github.rrs671.http.nio.rest.client.request.strategy.request.strategies;

import com.github.rrs671.http.nio.rest.client.request.strategy.request.RequestStrategy;
import com.github.rrs671.http.nio.rest.utils.AsyncExecutorUtils;
import com.github.rrs671.http.nio.rest.utils.RequestParams;
import org.springframework.web.client.RestClient;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

public class DeleteRequestStrategy implements RequestStrategy {

    public CompletableFuture<Void> deleteRequest(ExecutorService executor, Semaphore semaphore, RestClient restClient, RequestParams params, String url) {
        return AsyncExecutorUtils.asyncRequest(executor, semaphore, () -> {
            RestClient.RequestHeadersSpec<?> spec = restClient.delete().uri(url);

            if (Objects.nonNull(params.getHeaders())) {
                params.getHeaders().forEach(spec::header);
            }

            spec.retrieve();
            return null;
        });
    }

}
