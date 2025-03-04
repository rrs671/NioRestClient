package com.github.rrs671.http.nio.rest.utils;

import com.github.rrs671.http.nio.rest.client.request.RequestExecutor;
import com.github.rrs671.http.nio.rest.exceptions.CommunicateException;
import com.github.rrs671.http.nio.rest.exceptions.HttpException;
import com.github.rrs671.http.nio.rest.exceptions.ProcessException;
import com.github.rrs671.http.nio.rest.http.AsyncRequest;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AsyncExecutorUtils {

    private static ExecutorService globalExecutor;
    private static final AtomicLong unprocessedRequests = new AtomicLong(0);

    private AsyncExecutorUtils() {}

    public static synchronized ExecutorService getGlobalExecutorInstance() {
        if (globalExecutor == null) {
            globalExecutor = Executors.newVirtualThreadPerTaskExecutor();
        }

        return globalExecutor;
    }

    public static long pendingRequests() {
        return unprocessedRequests.get();
    }

    public static void incrementRequest() {
        unprocessedRequests.incrementAndGet();
    }

    public static <T> CompletableFuture<T> asyncRequest(ExecutorService executorService, Semaphore semaphore, ClientParams clientParams, boolean isScheduled, RequestExecutor<T> requestExecutor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                semaphore.acquire();
                return executeRequest(requestExecutor, semaphore, clientParams, isScheduled);
            } catch (InterruptedException e) {
                unprocessedRequests.decrementAndGet();
                semaphore.release();
                throw new RuntimeException(e);
            }
        }, executorService);
    }

    public static <T> AsyncRequest<T> returnAsyncResponse(CompletableFuture<T> future, ExecutorService globalExecutor) {
        return new AsyncRequest<>(CompletableFuture.supplyAsync(() -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new CompletionException(e);
            }
        }, globalExecutor));
    }

    private static void applyDelay(ClientParams clientParams) {
        try {
            Thread.sleep(clientParams.getDelayInMilliSeconds());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static  <T> T executeRequest(RequestExecutor<T> executor, Semaphore semaphore, ClientParams clientParams, boolean isScheduled) {
        try {
            return executor.execute();
        } catch (HttpStatusCodeException e) {
            throw new HttpException(e);
        } catch (ResourceAccessException e) {
            throw new CommunicateException(e.getMessage()) ;
        } catch (Exception e) {
            throw new ProcessException(e.getMessage());
        } finally {
            if (isScheduled && unprocessedRequests.get() > 1L && clientParams.getMaxConcurrentRequests() < unprocessedRequests.get()) {
                applyDelay(clientParams);
            }

            unprocessedRequests.decrementAndGet();
            semaphore.release();
        }
    }

    public static boolean isParalell() {
        int cores = Runtime.getRuntime().availableProcessors();
        return cores > 1;
    }

}
