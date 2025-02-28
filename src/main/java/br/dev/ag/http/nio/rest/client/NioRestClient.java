package br.dev.ag.http.nio.rest.client;

import br.dev.ag.http.nio.rest.client.request.RestRequest;

/**
 * NioRestClient class, used to build RestRequests.
 *
 * @since 0.0.1
 */
public class NioRestClient {

    /**
     * Returns a RestRequest instance
     *
     * @param timeoutInSeconds timeout in seconds for connection and read
     * @return a RestRequest instance
     */
    public RestRequest rest(int timeoutInSeconds) {
        return new RestRequest(timeoutInSeconds);
    }

    /**
     * Returns a RestRequest instance
     *
     * @param timeoutInSeconds timeout in seconds for connection and read
     * @param maxConcurrentRequests maximum concurrent requests for this RestRequest
     * @return a RestRequest instance
     */
    public RestRequest rest(int timeoutInSeconds, int maxConcurrentRequests) {
        return new RestRequest(timeoutInSeconds, maxConcurrentRequests);
    }

}
