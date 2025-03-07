package com.github.rrs671.http.nio.rest.client.request.strategy.request.strategies;

import com.github.rrs671.http.nio.rest.client.request.strategy.request.RequestStrategy;
import com.github.rrs671.http.nio.rest.utils.AsyncExecutorUtils;
import com.github.rrs671.http.nio.rest.utils.ClientParams;
import com.github.rrs671.http.nio.rest.utils.RequestParams;
import org.springframework.web.client.RestClient;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

public class GetRequestStrategy implements RequestStrategy {

    public <T> CompletableFuture<T> getRequest(ExecutorService executor, Semaphore semaphore, RestClient restClient,
                                               RequestParams params, Class<T> clazz, String url, ClientParams clientParams, boolean isScheduled) {
        return AsyncExecutorUtils.asyncRequest(executor, semaphore, clientParams, isScheduled, () -> {
            RestClient.RequestHeadersSpec<?> spec = restClient.get().uri(url);

            if (Objects.nonNull(params.getHeaders())) {
                params.getHeaders().forEach(spec::header);
            }
            return spec.retrieve().body(clazz);
        });
    }

}
