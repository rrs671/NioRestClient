package com.github.rrs671.http.nio.rest.client.request.strategy.scheduled_request.strategies;

import com.github.rrs671.http.nio.rest.client.request.strategy.scheduled_request.ScheduledRequestStrategy;
import com.github.rrs671.http.nio.rest.utils.AsyncExecutorUtils;
import com.github.rrs671.http.nio.rest.utils.RequestParams;
import org.springframework.web.client.RestClient;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

public class DeleteScheduledRequestStrategy implements ScheduledRequestStrategy {

    public Future<Void> deleteScheduled(ExecutorService executor, Semaphore semaphore, RestClient restClient, RequestParams params, String url) {
        return AsyncExecutorUtils.asyncScheduledRequest(() -> {
            RestClient.RequestHeadersSpec<?> spec = restClient.delete().uri(url);

            if (Objects.nonNull(params.getHeaders())) {
                params.getHeaders().forEach(spec::header);
            }

            spec.retrieve();
            return null;
        }, executor, semaphore);
    }

}
