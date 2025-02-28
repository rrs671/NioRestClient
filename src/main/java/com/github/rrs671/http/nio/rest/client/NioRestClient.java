package com.github.rrs671.http.nio.rest.client;

import com.github.rrs671.http.nio.rest.client.request.RestRequest;
import com.github.rrs671.http.nio.rest.utils.HttpTimeoutParams;

/**
 * NioRestClient class, used to build RestRequests.
 *
 * @since 0.0.1
 */
public class NioRestClient {

    /**
     * Returns a RestRequest instance
     *
     * @param httpTimeoutParams a object that contains connection and read timeout values
     * @return a RestRequest instance
     */
    public RestRequest rest(HttpTimeoutParams httpTimeoutParams) {
        return new RestRequest(httpTimeoutParams);
    }

    /**
     * Returns a RestRequest instance
     *
     * @param httpTimeoutParams a object that contains connection and read timeout values
     * @param maxConcurrentRequests maximum concurrent requests for this RestRequest
     * @return a RestRequest instance
     */
    public RestRequest rest(HttpTimeoutParams httpTimeoutParams, int maxConcurrentRequests) {
        return new RestRequest(httpTimeoutParams, maxConcurrentRequests);
    }

}
