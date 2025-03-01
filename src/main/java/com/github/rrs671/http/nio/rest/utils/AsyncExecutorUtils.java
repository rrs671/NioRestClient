package com.github.rrs671.http.nio.rest.utils;

import com.github.rrs671.http.nio.rest.client.request.RequestExecutor;
import com.github.rrs671.http.nio.rest.exceptions.CommunicateException;
import com.github.rrs671.http.nio.rest.exceptions.HttpException;
import com.github.rrs671.http.nio.rest.exceptions.ProcessException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import java.util.Objects;
import java.util.concurrent.*;

public abstract class AsyncExecutorUtils {

    private AsyncExecutorUtils() {}

    public static <T> Future<T> asyncScheduledRequest(RequestExecutor<T> executor, ExecutorService executorService, Semaphore semaphore) {
        return executorService.submit(() -> {
            try {
                semaphore.acquire();
                return executeRequest(executor);
            } catch (InterruptedException e) {
                semaphore.release();
                throw new RuntimeException(e);
            }
        });
    }

    public static <T> CompletableFuture<T> returnAsyncScheduledResponse(Future<T> future, ExecutorService globalExecutor, Semaphore semaphore, NioRestClientParams nioRestClientParams) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new CompletionException(e);
            } finally {
                applyDelay(semaphore, nioRestClientParams);
            }
        }, globalExecutor);
    }

    public static <T> CompletableFuture<T> asyncRequest(ExecutorService defaultExecutor, RequestExecutor<T> requestExecutor) {
        return CompletableFuture.supplyAsync(() -> executeRequest(requestExecutor), defaultExecutor);
    }

    private static void applyDelay(Semaphore semaphore, NioRestClientParams nioRestClientParams) {
        try {
            Thread.sleep(nioRestClientParams.getDelay() * 1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            semaphore.release();
        }
    }

    public static ExecutorService getExecutorService(ExecutorService limitedThreadsExecutor, ExecutorService globalExecutor) {
        return (Objects.isNull(limitedThreadsExecutor)) ? globalExecutor : limitedThreadsExecutor;
    }

    public static  <T> T executeRequest(RequestExecutor<T> executor) {
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

    public static boolean processOnParalell() {
        int cores = Runtime.getRuntime().availableProcessors();
        return cores > 1;
    }

}
