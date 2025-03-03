package com.github.rrs671.http.nio.rest.utils;

import com.github.rrs671.http.nio.rest.client.request.RequestExecutor;
import com.github.rrs671.http.nio.rest.exceptions.CommunicateException;
import com.github.rrs671.http.nio.rest.exceptions.HttpException;
import com.github.rrs671.http.nio.rest.exceptions.ProcessException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import java.util.concurrent.*;

public abstract class AsyncExecutorUtils {

    private AsyncExecutorUtils() {}

    public static <T> CompletableFuture<T> asyncRequest(ExecutorService executorService, Semaphore semaphore, RequestExecutor<T> requestExecutor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                semaphore.acquire();
                return executeRequest(requestExecutor);
            } catch (InterruptedException e) {
                semaphore.release();
                throw new RuntimeException(e);
            }
        }, executorService);
    }

    public static <T> CompletableFuture<T> returnAsyncResponse(CompletableFuture<T> future, ExecutorService globalExecutor, Semaphore semaphore, NioRestClientParams nioRestClientParams, boolean isScheduled) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new CompletionException(e);
            } finally {
                if (isScheduled) {
                    applyDelay(semaphore, nioRestClientParams);
                }

                semaphore.release();
            }
        }, globalExecutor);
    }

    private static void applyDelay(Semaphore semaphore, NioRestClientParams nioRestClientParams) {
        try {
            Thread.sleep(nioRestClientParams.getDelay() * 1000L);
        } catch (InterruptedException e) {
            semaphore.release();
            throw new RuntimeException(e);
        }
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

    public static boolean isParalell() {
        int cores = Runtime.getRuntime().availableProcessors();
        return cores > 1;
    }

}
