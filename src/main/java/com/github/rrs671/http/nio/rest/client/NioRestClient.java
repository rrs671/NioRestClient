package com.github.rrs671.http.nio.rest.client;

import com.github.rrs671.http.nio.rest.client.request.RestRequest;
import com.github.rrs671.http.nio.rest.utils.NioRestClientParams;

/**
 * NioRestClient class, used to build RestRequests.
 *
 * @since 0.0.1
 */
public class NioRestClient {

    /**
     * Returns a RestRequest instance with infinite connection and read timeout and without max concurrent requests limit
     *
     * @return a RestRequest instance
     */
    public RestRequest rest() {
        NioRestClientParams nioRestClientParams = NioRestClientParams.builder()
                .addReadTimeout(0)
                .addConnectionTimeout(0)
                .addMaxConcurrentRequest(0)
                .build();

        return new RestRequest(nioRestClientParams);
    }

    /**
     * Returns a RestRequest instance
     *
     * @param timeoutInSeconds connection and read timeout values in seconds and without max concurrent requests limit
     * @return a RestRequest instance
     */
    public RestRequest rest(int timeoutInSeconds) {
        NioRestClientParams nioRestClientParams = NioRestClientParams.builder()
                .addReadTimeout(timeoutInSeconds)
                .addConnectionTimeout(timeoutInSeconds)
                .addMaxConcurrentRequest(0)
                .build();

        return new RestRequest(nioRestClientParams);
    }

    /**
     * Returns a RestRequest instance
     *
     * @param timeoutInSeconds connection and read timeout values in seconds
     * @param maxConcurrentRequests maximum concurrent requests for this RestRequest
     * @return a RestRequest instance
     */
    public RestRequest rest(int timeoutInSeconds, int maxConcurrentRequests) {
        NioRestClientParams nioRestClientParams = NioRestClientParams.builder()
                .addReadTimeout(timeoutInSeconds)
                .addConnectionTimeout(timeoutInSeconds)
                .addMaxConcurrentRequest(maxConcurrentRequests)
                .build();

        return new RestRequest(nioRestClientParams);
    }

    /**
     * Returns a RestRequest instance
     *
     * @param nioRestClientParams a object that contains NioRestClient parameters like connection and read timeout values in seconds
     *                            and max concurrent requests
     * @return a RestRequest instance
     */
    public RestRequest rest(NioRestClientParams nioRestClientParams) {
        return new RestRequest(nioRestClientParams);
    }

}
