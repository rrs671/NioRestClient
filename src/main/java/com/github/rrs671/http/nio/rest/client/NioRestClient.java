package com.github.rrs671.http.nio.rest.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rrs671.http.nio.rest.client.request.RestRequest;
import com.github.rrs671.http.nio.rest.utils.ClientParams;

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
        ClientParams clientParams = ClientParams.builder()
                .addReadTimeout(0)
                .addConnectionTimeout(0)
                .addMaxConcurrentRequest(0)
                .build();

        return new RestRequest(clientParams);
    }

    /**
     * Returns a RestRequest instance
     *
     * @param timeoutInSeconds connection and read timeout values in seconds and without max concurrent requests limit
     * @return a RestRequest instance
     */
    public RestRequest rest(int timeoutInSeconds) {
        ClientParams clientParams = ClientParams.builder()
                .addReadTimeout(timeoutInSeconds)
                .addConnectionTimeout(timeoutInSeconds)
                .addMaxConcurrentRequest(0)
                .build();

        return new RestRequest(clientParams);
    }

    /**
     * Returns a RestRequest instance
     *
     * @param timeoutInSeconds connection and read timeout values in seconds
     * @param maxConcurrentRequests maximum concurrent requests for this RestRequest
     * @return a RestRequest instance
     */
    public RestRequest rest(int timeoutInSeconds, int maxConcurrentRequests) {
        ClientParams clientParams = ClientParams.builder()
                .addReadTimeout(timeoutInSeconds)
                .addConnectionTimeout(timeoutInSeconds)
                .addMaxConcurrentRequest(maxConcurrentRequests)
                .build();

        return new RestRequest(clientParams);
    }

    /**
     * Returns a RestRequest instance
     *
     * @param clientParams a object that contains NioRestClient parameters like connection and read timeout values in seconds
     *                            and max concurrent requests
     * @return a RestRequest instance
     */
    public RestRequest rest(ClientParams clientParams) {
        return new RestRequest(clientParams);
    }

    /**
     * Returns a RestRequest instance
     *
     * @param clientParams a object that contains NioRestClient parameters like connection and read timeout values in seconds
     *                            and max concurrent requests
     * @param objectMapper a custom Object Mapper if it is necessary
     * @return a RestRequest instance
     */
    public RestRequest rest(ClientParams clientParams, ObjectMapper objectMapper) {
        return new RestRequest(clientParams, objectMapper);
    }

}
