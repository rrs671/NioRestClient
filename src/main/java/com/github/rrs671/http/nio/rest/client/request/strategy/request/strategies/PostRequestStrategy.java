package com.github.rrs671.http.nio.rest.client.request.strategy.request.strategies;

import com.github.rrs671.http.nio.rest.client.request.strategy.request.RequestStrategy;
import com.github.rrs671.http.nio.rest.utils.AsyncExecutorUtils;
import com.github.rrs671.http.nio.rest.utils.RequestParams;
import org.springframework.web.client.RestClient;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

public class PostRequestStrategy implements RequestStrategy {

    public <T, R> CompletableFuture<T> postRequest(ExecutorService executor, Semaphore semaphore, RestClient restClient, RequestParams params, R body, Class<T> clazz, String url) {
        return AsyncExecutorUtils.asyncRequest(executor, semaphore, () -> {
            RestClient.RequestBodySpec spec = restClient.post().uri(url);

            if (Objects.nonNull(params.getHeaders())) {
                params.getHeaders().forEach(spec::header);
            }

            return spec.body(body).retrieve().body(clazz);
        });
    }

}
